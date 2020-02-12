(ns portable-text.html
  (:require [clojure.string :as str]
            [clojure.set :as set]
            [portable-text.image :as image]))

(defn children? [xs]
  (or (seq? xs)
      (and (vector? xs)
           (-> xs first keyword? not))))

(defn attr-str [attrs]
  (if-not (empty? attrs)
    (str " "
         (->> attrs
              (map (fn [[k v]] (format "%s=\"%s\"" (name k) v)))
              (str/join " ")))
    ""))

(def self-closing
  #{:img :br})

(def escaped-chars
  {\' "&#x27;"
   \" "&quot;"
   \> "&gt;"})

(defn to-html [x]
  (cond
    (string? x) (str/escape x escaped-chars)
    (children? x) (->> x
                       (map to-html)
                       (str/join ""))
    (nil? x) ""
    :default (let [[tag attrs & content] x
                   tag-name (name tag)]
               (if (self-closing tag)
                 (format "<%s%s/>" tag-name (attr-str attrs))
                 (format "<%s%s>%s</%s>" tag-name (attr-str attrs) (to-html content) tag-name)))))

(defmulti render-block (fn [opt {:keys [_type]}] (keyword _type)))

(defn el [tag attr children]
  (if (vector? children)
    [tag attr children]
    (apply vector tag attr children)))

(defn hiccup? [x]
  (and (vector? x)
       (keyword? (first x))))

(defn merge-same-tag-siblings [children]
  (->> children
       (partition-by #(take 2 %))
       (mapcat (fn [xs]
                 (if (hiccup? (first xs))
                   (let [[tag attrs] (first xs)]
                     [(el tag attrs (merge-same-tag-siblings
                                     (mapcat #(drop 2 %) xs)))])
                   xs)))))

(defn- map-by [k xs]
  (->> xs
       (map (fn [x] [(k x) x]))
       (into {})))

(def tag-names
  {"link" :a
   "normal" :p
   "bullet" :ul
   "number" :ol
   "underline" :span
   "strike-through" :del
   "highlight" :span
   "em" :em
   "strong" :strong
   "code" :code
   "h1" :h1
   "h2" :h2
   "h3" :h3
   "h4" :h4
   "h5" :h5
   "h6" :h6
   "blockquote" :blockquote})

(defn tag-name [style & [block]]
  (or (when (:listItem block) :li)
      (tag-names style)
      (when (= "block" (:_type block)) :p)
      (when (keyword? style) style)
      :span))

(defn with-siblings [xs]
  (take (count xs) (partition-all 3 1 (concat [nil] xs))))

(defn coll-order [xs]
  (fn [x]
    (.indexOf xs x)))

(defn sort-marks
  "Tries to align each child's marks to optimize for the
  smallest possible HTML output."
  [children]
  (->> children
       with-siblings
       (map (fn [[before el after]]
              (let [w-before (set/intersection (set (:marks before)) (set (:marks el)))
                    w-after (set/intersection (set (:marks after)) (set (:marks el)))
                    sort-marks (reverse (if (< (count w-before) (count w-after))
                                          (sort-by (coll-order (:marks el)) w-after)
                                          (sort-by (coll-order (:marks before)) w-before)))
                    sort-order (fn [m] (.indexOf sort-marks m))]
                (update el :marks #(sort-by (comp - sort-order) %)))))))

(defn- mark-attr-pair [mark-def k v]
  (cond
    (and (= (:_type mark-def) "highlight")
         (= k :thickness))
    [:style (format "border:%spx solid;" v)]

    :default [k v]))

(defn- mark-def-attrs [def]
  (->> def
       (keep (fn [[k v]]
               (when-not (str/starts-with? (name k) "_")
                 (mark-attr-pair def k v))))
       (into {})))

(defn- mark-attrs [mark]
  (if (= "underline" mark)
    {:style "text-decoration:underline;"}
    {}))

(defn block-hiccup [mark-defs mark content]
  (if-let [def (get mark-defs mark)]
    (let [tag (tag-name (:_type def))
          attrs (mark-def-attrs def)]
      (el tag attrs content))
    (el (tag-name mark) (mark-attrs mark) content)))

(defn text-content [text]
  (->> (str/split text #"\n")
       (interpose [:br])))

(defmethod render-block :span [opt {:keys [marks text] :as block}]
  (loop [content (text-content text)
         [mark & marks] (reverse marks)]
    (if (nil? mark)
      content
      (recur (block-hiccup (:_defs opt) mark content) marks))))

(defmethod render-block :code [opt {:keys [language code]}]
  [:pre {:data-language language}
   [:code {} code]])

(defmethod render-block :button [opt {:keys [text]}]
  (block-hiccup nil :button (text-content text)))

(defmethod render-block :block [opt {:keys [style children markDefs] :as block}]
  (let [opt (assoc opt :_defs (map-by :_key markDefs))
        content (->> children
                     sort-marks
                     (map #(render-block (assoc opt ::inline? true) %))
                     merge-same-tag-siblings)]
    (el (tag-name style block) {} content)))

(defmethod render-block :image [opt {:keys [asset]}]
  (let [img [:img {:src (image/image-url opt asset)}]]
    (if (::inline? opt)
      img
      [:figure {} img])))

(defn list-level [list-item]
  (get list-item :level 1))

(defn same-list? [list-item x]
  (or (= (select-keys list-item [:level :listItem])
         (select-keys x [:level :listItem]))
      (< (list-level list-item) (list-level x))))

(declare render-lists)

(defn list-item [xs]
  (let [[item & nested-items] xs
        [tag attr children] (render-block {} item)]
    [tag attr (concat (if (contains? #{nil "normal"} (:style item))
                        children
                        (list [(some-> item :style tag-name) {} children]))
                      (render-lists nested-items))]))

(defn render-list [items]
  (loop [[x & xs] items
         list-items []]
    (if x
      (let [children? (comp (partial < (list-level x)) list-level)]
        (recur (drop-while children? xs) (conj list-items (concat [x] (take-while children? xs)))))
      [(tag-names (-> items first :listItem)) {} (map list-item list-items)])))

(defn render-lists [list-blocks]
  (loop [xs list-blocks
         lists []]
    (if (seq xs)
      (let [same-list? (partial same-list? (first xs))]
        (recur (drop-while same-list? xs) (conj lists (take-while same-list? xs))))
      (map render-list lists))))

(defn to-hiccup
  ([blocks] (to-hiccup blocks {}))
  ([blocks opt]
   (let [blocks (if (sequential? blocks) blocks [blocks])]
     (let [content (->> blocks
                        (partition-by (comp nil? :listItem))
                        (mapcat (fn [xs]
                                  (if (-> xs first :listItem)
                                    (render-lists xs)
                                    (map #(render-block opt %) xs)))))]
       (cond
         (empty? content) nil
         (= 1 (count content)) (first content)
         :default [:div {} content])))))

(defn render
  ([blocks] (render blocks {}))
  ([blocks opt]
   (-> blocks
       (to-hiccup opt)
       to-html)))

(ns portable-text.html
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [clojure.walk :as walk]
            [portable-text.image :as image]))

(defn listItem [x]
  (or (:listItem x) (:list-item x)))

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

(defn parse-hiccup-vector [x]
  (let [[attrs & content] (cond-> (rest x)
                            (not (map? (second x))) (into [{}]))
        sym (name (first x))
        [_ id] (re-find #"#([^\.#]+)" sym)
        [el & classes] (-> (str/replace sym #"#([^#\.]+)" "")
                           (str/split #"\."))]
    (into
     [(keyword el)
      (cond-> attrs
        id (assoc :id id)
        (seq classes) (update :class #(str/join " " (if % (conj classes %) classes))))]
     content)))

(defn to-html [x]
  (cond
    (string? x) (str/escape x escaped-chars)
    (children? x) (->> x
                       (map to-html)
                       (str/join ""))
    (nil? x) ""
    :default (let [[tag attrs content] (parse-hiccup-vector x)
                   tag-name (name tag)]
               (if (self-closing tag)
                 (format "<%s%s/>" tag-name (attr-str attrs))
                 (format "<%s%s>%s</%s>"
                         tag-name
                         (attr-str attrs)
                         (to-html (drop 2 x))
                         tag-name)))))

(defmulti render-block (fn [opt {:keys [_type]}] (keyword _type)))

(defn render-hiccup-block [opt block]
  (let [res (render-block opt block)]
    (if (or (not (vector? res))
            (not (keyword? (first res)))
            (map? (second res))
            (and (vector? res) (= 1 (count res))))
      (if (seq? res)
        res
        (list res))
      (list (apply vector (first res) {} (rest res))))))

(defn el [tag attr children]
  (cond-> [tag]
    (or (not-empty attr)
        (not-empty children)) (conj attr)
    (vector? children) (conj children)
    (and
     (not (vector? children))
     (not-empty children)) (into children)))

(defn hiccup? [x]
  (and (not (map-entry? x))
       (vector? x)
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

(defn combine-sibling-strings [xs]
  (->> (partition-by string? xs)
       (mapcat #(if (string? (first %))
                  [(str/join %)]
                  %))))

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
  (or (when (listItem block) :li)
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
               (when (and (not (str/starts-with? (name k) "_"))
                          (or (string? v) (number? v) (boolean? v)))
                 (mark-attr-pair def k v))))
       (into {})))

(defn- mark-attrs [mark]
  (if (= "underline" mark)
    {:style "text-decoration:underline;"}
    {}))

(defmulti render-mark (fn [opt mark content] (keyword (:_type mark))))

(defmethod render-mark :default [opt mark-def content]
  (let [tag (tag-name (:_type mark-def))
        attrs (mark-def-attrs mark-def)]
    (el tag attrs content)))

(defn- flatten-seqs [x]
  (let [flattenable? #(or (list? %) (seq? %))]
    (->> (tree-seq flattenable? seq x)
         rest
         (filter (complement flattenable?)))))

(defn flatten-hiccup [x]
  (walk/postwalk
   (fn [form]
     (if (hiccup? form)
       (let [has-attrs? (map? (second form))]
         (el
          (first form)
          (if has-attrs? (second form) {})
          (flatten-seqs (drop (if has-attrs? 2 1) form))))
       form))
   x))

(defn block-hiccup [opt mark content]
  (if-let [def (get-in opt [:_defs mark])]
    (flatten-hiccup (render-mark opt def content))
    (el (tag-name mark) (mark-attrs mark) content)))

(defn text-content [text]
  (->> (str/split text #"\n")
       (interpose [:br])))

(defmethod render-block :span [opt {:keys [marks text] :as block}]
  (loop [content (text-content text)
         [mark & marks] (reverse marks)]
    (if (nil? mark)
      content
      (recur (block-hiccup opt mark content) marks))))

(defmethod render-block :code [opt {:keys [language code]}]
  [:pre {:data-language language}
   [:code {} code]])

(defmethod render-block :button [opt {:keys [text]}]
  (block-hiccup nil :button (text-content text)))

(defmethod render-block :block [opt {:keys [style children] :as block}]
  (let [mark-defs (or (:markDefs block) (:mark-defs block))
        opt (assoc opt :_defs (map-by :_key mark-defs))
        content (->> children
                     sort-marks
                     (mapcat #(render-hiccup-block (assoc opt ::inline? true) %))
                     merge-same-tag-siblings
                     combine-sibling-strings)]
    (el (tag-name style block) {} content)))

(defmethod render-block :image [opt {:keys [asset]}]
  (let [img [:img {:src (image/image-url opt asset)}]]
    (if (::inline? opt)
      img
      [:figure {} img])))

(defn list-level [list-item]
  (get list-item :level 1))

(defn same-list? [list-item x]
  (or (= (select-keys list-item [:level :listItem :list-item])
         (select-keys x [:level :listItem :list-item]))
      (< (list-level list-item) (list-level x))))

(declare render-lists)

(defn list-item [xs]
  (let [[item & nested-items] xs
        [tag attr & children] (render-block {} item)]
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
      [(tag-names (or (-> items first listItem))) {} (map list-item list-items)])))

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
                        (remove nil?)
                        (partition-by (comp nil? listItem))
                        (mapcat (fn [xs]
                                  (if (-> xs first listItem)
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

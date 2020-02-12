(ns portable-text.image)

(defmulti image-type (fn [opt block]))

(defmethod image-type :default [opt block] :image.type/sanity-cdn)

(defmulti image-url image-type)

(defmethod image-url :default [opt {:keys [path _ref]}]
  (let [cdn (or (:cdn-url opt "https://cdn.sanity.io"))]
    (if path
      (format "%s/%s" cdn path)
      (let [[_ asset-name suffix] (re-find #"image-(.*)-([^\-]+)$" _ref)]
        (format "%s/images/%s/%s/%s.%s"
                cdn
                (:sanity/project-id opt)
                (:sanity/dataset opt)
                asset-name
                suffix)))))

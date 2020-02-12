(ns portable-text.tests
  (:require [cheshire.core :as json]
            [clojure.pprint :refer [pprint]]
            [clojure.string :as str]
            [clojure.java.io :as io]))

(defn test-name [n]
  (let [[_ num test-name] (re-find #"(\d\d\d)-(.*).json" n)]
    (str test-name "-" num)))

(defn escape-quotes [s]
  (some-> s (str/replace #"\"" "\\\"")))

(defn format-edn [x]
  (str/trim (escape-quotes (with-out-str (pprint x)))))

(defn make-test [f]
  (let [{:keys [input output]} (json/parse-string (slurp f) true)]
    (println (format "(deftest %s
  (is (= (sut/render
           %s)
         %s)))
" (test-name (.getName f)) (format-edn input) (pr-str output)))))

(comment

  (for [f (sort-by #(.getName %) (drop 1 (file-seq (io/file "dev-resources"))))]
    (make-test f))

)

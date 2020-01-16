(ns fahrenheit-lang.compiler.csl
  (:require
    [clojure.zip :as zip]
    [clojure.data.xml :as xml]))

(defmulti info-statement->csl #(-> % second keyword))

(defmethod info-statement->csl :title
  [[_ _ text]]
  [:title text])

(defmethod info-statement->csl :author
  [[_ _ name email]]
  [:author
    [:name name]
    [:email email]])

(defn info->csl [ast]
  (let [loc (zip/vector-zip ast)]
    (-> loc
        (zip/down)
        (zip/right)
        (zip/edit
          (fn [[head & tail]]
            (into [head]
              (map info-statement->csl tail))))
        (zip/root))))

(defn transform [ast]
  (-> ast
      info->csl))
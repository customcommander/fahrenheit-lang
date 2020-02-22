(ns fahrenheit-lang.compiler.csl
  (:require [clojure.data.xml :as xml]
            [clojure.zip :as zip]
            [clojure.string :as str]))

(defmulti ast->xml zip/node)

(defmethod ast->xml :default [loc] loc)

(defmethod ast->xml :program [loc]
  (zip/replace loc :style))

(defmethod ast->xml :about [loc]
  (as-> loc l
        (zip/replace l :info)
        (zip/next l)
        (zip/replace l [:title (zip/node l)])))

(defmethod ast->xml :url [loc]
  (as-> loc l
        (zip/replace l :link)
        (zip/next l)
        (zip/edit l #(assoc {:rel "self"} :href %))))

(defmethod ast->xml :documentation [loc]
  (as-> loc l
        (zip/replace l :link)
        (zip/next l)
        (zip/edit l #(assoc {:rel "documentation"} :href %))))

(defmethod ast->xml :template [loc]
  (as-> loc l
        (zip/replace l :link)
        (zip/next l)
        (zip/edit l #(assoc {:rel "template"} :href %))))

(defmethod ast->xml :author [loc]
  (as-> loc l
        (zip/next l)
        (zip/edit l #(conj [:name] %))))

(defmethod ast->xml :author-extended [loc]
  (as-> loc l
        (zip/replace l :author)
        (zip/next l)
        (zip/edit l #(conj [:name] %))))

(defmethod ast->xml :website [loc]
  (zip/replace loc :uri))

(defmethod ast->xml :citation-format [loc]
  (as-> loc l
        (zip/replace l :category)
        (zip/next l)
        (zip/edit l #(assoc {} :citation-format %))))

(defmethod ast->xml :field [loc]
  (let [field (first (zip/rights loc))
        fields (map str/trim (str/split field #","))]
    (if (= 1 (count fields))
      (-> loc
          (zip/replace :category)
          (zip/next)
          (zip/replace {:field (first fields)}))
      (loop [l (zip/up loc)
             f fields]
        (if (empty? f)
          (zip/remove l)
          (recur (zip/insert-right l [:category {:field (first f)}])
                 (rest f)))))))

(defn ast->csl [ast]
  (loop [loc (zip/vector-zip ast)]
    (if (zip/end? loc)
      (-> loc
          zip/root
          xml/sexp-as-element
          xml/indent-str)
      (recur (-> loc ast->xml zip/next)))))

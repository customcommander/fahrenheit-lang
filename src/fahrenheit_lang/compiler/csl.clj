(ns fahrenheit-lang.compiler.csl
  (:require [clojure.data.xml :as xml]
            [clojure.zip :as zip]))

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

(defn ast->csl [ast]
  (loop [loc (zip/vector-zip ast)]
    (if (zip/end? loc)
      (-> loc
          zip/root
          xml/sexp-as-element
          xml/indent-str)
      (recur (-> loc ast->xml zip/next)))))

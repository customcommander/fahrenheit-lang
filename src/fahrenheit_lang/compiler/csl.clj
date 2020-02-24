(ns fahrenheit-lang.compiler.csl
  (:require [clojure.data.xml :as xml]
            [clojure.zip :as zip]
            [clojure.string :as str]))

(derive ::author ::person)

(derive ::title ::node-with-lang)
(derive ::title-short ::node-with-lang)
(derive ::summary ::node-with-lang)

(derive ::url ::link)
(derive ::documentation ::link)
(derive ::template ::link)

; I don't like hardcoding the namespace as a string but I couldn't find another way.
; I tried is to use `(str *ns*)` but that returned "user" instead of "fahrenheit-lang.compiler.csl"!?
; This is a minor technical debt but I'd like to see this fixed.
(defn keyword->qualified-keyword [k]
  (keyword "fahrenheit-lang.compiler.csl" (name k)))

(defmulti ast->xml
  (fn [loc]
    (let [node (zip/node loc)]
      (if (keyword? node)
        (keyword->qualified-keyword node)
        node))))

(defmethod ast->xml :default [loc] loc)

(defmethod ast->xml ::program [loc]
  (zip/replace loc :style))

(defmethod ast->xml ::about [loc]
  (zip/replace loc :info))

(defmethod ast->xml ::node-with-lang [loc]
  (let [node (zip/node loc)
        [content lang] (zip/rights loc)]
    (if (nil? lang)
      loc
      (-> loc
          (zip/up)
          (zip/replace [node {:xml:lang lang} content])
          (zip/right)))))

(defmethod ast->xml ::link [loc]
  (let [node (zip/node loc)
        [uri lang] (zip/rights loc)
        rel (node {:url "self"
                   :template "template"
                   :documentation "documentation"})]
    (-> loc
        (zip/up)
        (zip/replace [:link (merge {:rel rel :href uri}
                                   (when-not (nil? lang) {:xml:lang lang}))])
        (zip/right))))

(defmethod ast->xml ::person [loc]
  (-> loc
      (zip/next)
      (zip/edit #(conj [:name] %))))

(defmethod ast->xml ::website [loc]
  (zip/replace loc :uri))

(defmethod ast->xml ::citation-format [loc]
  (as-> loc l
        (zip/replace l :category)
        (zip/next l)
        (zip/edit l #(assoc {} :citation-format %))))

(defmethod ast->xml ::field [loc]
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

(ns fahrenheit-lang.compiler.csl
  (:require [clojure.data.xml :as xml]
            [clojure.zip :as zip]
            [clojure.string :as str]))

(defn tailstr [s]
  "Returns string s without its first character."
  (str/join (rest s)))

(defn clean-map [m]
  (into {} (filter (comp some? val) m)))

(derive ::author ::person)
(derive ::contributor ::person)

(derive ::title ::node-with-lang)
(derive ::title-short ::node-with-lang)
(derive ::summary ::node-with-lang)

(derive ::url ::link)
(derive ::documentation ::link)
(derive ::template ::link)

(derive ::var ::printable)

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

; based on https://spdx.org/licenses/
(defmethod ast->xml ::license [loc]
  (let [identifier (keyword (zip/node (zip/next loc)))
        fullname (identifier {:CC-BY-SA-3.0 "Creative Commons Attribution Share Alike 3.0 Unported"})
        uri (identifier {:CC-BY-SA-3.0 "https://creativecommons.org/licenses/by-sa/3.0/"})]
    (-> loc
        (zip/up)
        (zip/replace [:rights {:license uri :xml:lang "en"} fullname])
        (zip/right))))


(defmethod ast->xml ::citation [loc]
  (let [siblings (zip/rights loc)]
    (-> loc
        (zip/up)
        (zip/replace [:citation `[:layout ~@siblings]])
        (zip/down)
        (zip/next))))

(defmethod ast->xml ::layout [loc]
  (-> loc
      (zip/insert-right {})
      (zip/next)
      (zip/next)))

(defmethod ast->xml ::output-modifier [loc]
  (let [modifiers (zip/node (zip/next loc))]
    (-> loc
        (zip/up)
        (zip/remove)
        (zip/edit #(merge % modifiers)))))

(defmethod ast->xml ::print [loc]
  (-> loc
      (zip/replace :text)
      (zip/insert-right {})
      (zip/next)
      (zip/next)))

(defmethod ast->xml ::printable [loc]
  (let [[node content] (zip/children (zip/up loc))]
    (-> loc
        (zip/up)
        (zip/remove)
        (zip/edit #(merge % ((keyword node) {:var {:variable (tailstr content)}}))))))

(defn ast->csl [ast]
  (loop [loc (zip/vector-zip ast)]
    (if (zip/end? loc)
      (-> loc
          zip/root
          xml/sexp-as-element
          xml/indent-str)
      (recur (-> loc ast->xml zip/next)))))

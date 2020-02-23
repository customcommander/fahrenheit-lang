(ns fahrenheit-lang.compiler.csl
  (:require [clojure.data.xml :as xml]
            [clojure.zip :as zip]
            [clojure.string :as str]))

(defmulti ast->xml zip/node)

(defmethod ast->xml :default [loc] loc)

(defmethod ast->xml :program [loc]
  (zip/replace loc :style))

(defmethod ast->xml :about [loc]
  (zip/replace loc :info))

(defmethod ast->xml :title [loc]
  (let [[title lang] (zip/rights loc)]
    (if (nil? lang)
      loc
      (-> loc
          (zip/up)
          (zip/replace [:title {:xml:lang lang} title])
          (zip/right)))))

(defmethod ast->xml :title-short [loc]
  (let [[title lang] (zip/rights loc)]
    (if (nil? lang)
      loc
      (-> loc
          (zip/up)
          (zip/replace [:title-short {:xml:lang lang} title])
          (zip/right)))))

(defmethod ast->xml :summary [loc]
  (let [[summary lang] (zip/rights loc)]
    (if (nil? lang)
      loc
      (-> loc
          (zip/up)
          (zip/replace [:summary {:xml:lang lang} summary])
          (zip/right)))))

(defmethod ast->xml :url [loc]
  (let [[uri lang] (zip/rights loc)]
    (-> loc
        (zip/up)
        (zip/replace [:link (merge {:rel "self" :href uri}
                                   (when-not (nil? lang) {:xml:lang lang}))]))))

(defmethod ast->xml :documentation [loc]
  (let [[uri lang] (zip/rights loc)]
    (-> loc
        (zip/up)
        (zip/replace [:link (merge {:rel "documentation" :href uri}
                                   (when-not (nil? lang) {:xml:lang lang}))]))))

(defmethod ast->xml :template [loc]
  (let [[uri lang] (zip/rights loc)]
    (-> loc
        (zip/up)
        (zip/replace [:link (merge {:rel "template" :href uri}
                                   (when-not (nil? lang) {:xml:lang lang}))]))))

(defmethod ast->xml :author [loc]
  (as-> loc l
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

(ns fahrenheit-lang.compiler.csl
  (:require [clojure.data.xml :as xml]))

(defmulti ast->xml-info first)

(defmethod ast->xml-info :default [[k v]]
  [k {} v])

(defmethod ast->xml-info :url [[k v]]
  [:link {:href v :rel "self"}])

(defmethod ast->xml-info :template [[k v]]
  [:link {:href v :rel "template"}])

(defmethod ast->xml-info :documentation [[k v]]
  [:link {:href v :rel "documentation"}])

(defmethod ast->xml-info :author [[_ name]]
  [:author {}
    [:name {} name]])

(defmethod ast->xml-info :author-extended [[_ name & rest]]
  (let [author (into {} (cons [:name name] rest))]
    [:author {}
      [:name {} (:name author)]
      (when (:email author)
        [:email {} (:email author)])
      (when (:website author)
        [:uri {} (:website author)])]))

(defmulti ast->xml first)

(defmethod ast->xml :program [[_ & ast]]
  (->>  [:style {} (ast->xml (first ast))]
        xml/sexp-as-element
        xml/indent-str))

(defmethod ast->xml :style [[_ title & ast]]
  (let [info (map ast->xml-info (cons [:title title] ast))]
    `[:info {} ~@info]))

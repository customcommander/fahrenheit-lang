(ns fahrenheit-lang.parser
  (:require [instaparse.core :as insta]))

(def parser
  (insta/parser
    (clojure.java.io/resource "fahrenheit-grammar.ebnf")
    :auto-whitespace :standard))

(defn parse [source]
  (parser source))

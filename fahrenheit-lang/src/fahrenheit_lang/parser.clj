(ns fahrenheit-lang.parser
  (:require [instaparse.core :as insta]))

(def parser
  (insta/parser
    (clojure.java.io/resource "grammar.bnf")
    :auto-whitespace :standard))

(defn parse [source]
  (parser source))

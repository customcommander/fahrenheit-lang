(ns fahrenheit-lang.parser
  (:require [instaparse.core :as insta]))

(def font-weight
  {:bold "bold"
   :light-bold "light"})

(def display
  {:left "left-margin"
   :right "right-inline"
   :indent "indent"})

(def parser
  (insta/parser
    (clojure.java.io/resource "fahrenheit-grammar.ebnf")
    :auto-whitespace :standard))

(defn parse [source]
  (->>  (parser source)
        (insta/transform
          {:str str
           :prefix #(hash-map :prefix %)
           :suffix #(hash-map :suffix %)
           :delimiter #(hash-map :delimiter %)
           :font-style #(hash-map :font-style %)
           :font-variant #(hash-map :font-variant %)
           :font-weight #(hash-map :font-weight ((keyword %) font-weight))
           :display #(hash-map :display ((keyword %) display))
           :modifiers (fn [& m] [:modifiers (into {} m)])})))

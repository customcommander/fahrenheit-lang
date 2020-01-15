(ns fahrenheit-lang.compiler.csl
  (:require [clojure.zip :as zip]))

(defn transform [ast]
  (->> ast
      rest
      vec
      (reduce conj)))
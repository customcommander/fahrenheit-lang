(ns fahrenheit-lang.core
  (:gen-class)
  (:require
   [clojure.pprint :as pp]
   [clojure.tools.cli :refer [parse-opts]]
   [instaparse.core :as insta]))

(def parser
  (insta/parser
    (clojure.java.io/resource "grammar.bnf")
    :auto-whitespace :standard))

(def cli-options
  [["-f" "--file FILE" "*.fahr file"]])

(defn -main
  [& args]
  (let [{opts :options} (parse-opts args cli-options)]
    (->> (:file opts)
         (slurp)
         (parser)
         (pp/pprint))))

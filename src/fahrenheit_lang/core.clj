(ns fahrenheit-lang.core
  (:gen-class)
  (:require
   [clojure.pprint :as pp]
   [clojure.tools.cli :refer [parse-opts]]
   [fahrenheit-lang.parser :refer [parse]]
   [fahrenheit-lang.compiler.csl :as comp]))

(def cli-options
  [["-f" "--file FILE" "*.fahr file"]])

(defn -main
  [& args]
  (let [{opts :options} (parse-opts args cli-options)]
    (as-> (:file opts) x
          (slurp x)
          (parse x)
          (do (pp/pprint x) x)
          (comp/ast->csl x)
          (print x))))

(ns fahrenheit-lang.core
  (:gen-class)
  (:require
   [clojure.pprint :as pp]
   [clojure.tools.cli :refer [parse-opts]]
   [fahrenheit-lang.parser :refer [parse]]
   [fahrenheit-lang.compiler.csl :as c-csl]))

(def cli-options
  [["-f" "--file FILE" "*.fahr file"]])

(defn -main
  [& args]
  (let [{opts :options} (parse-opts args cli-options)]
    (->> (:file opts)
         (slurp)
         (parse)
         (c-csl/transform)
         (pp/pprint))))

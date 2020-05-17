(ns fahrenheit-lang.core
  (:gen-class)
  (:require
   [clojure.pprint :refer [pprint]]
   [clojure.tools.cli :refer [parse-opts]]
   [fahrenheit-lang.parser :refer [parse]]
   [fahrenheit-lang.transform :refer [transform]]
   [fahrenheit-lang.csl :as csl]))

(def cli-options
  [["-f" "--file FILE" "Path to *.fahr file"]
   [nil "--raw" "Parse *.fahr file and print unprocessed AST to standard output."
    :default false]
   [nil "--ast" "Parse *.fahr file and print preprocessed AST to standard output."
    :default false]
   [nil "--csl" "Generate CSL and print to standard output."
    :default true]])

(defn -main [& args]
  (let [{opts :options} (parse-opts args cli-options)
        parsed (-> opts :file slurp parse)]
    (cond
      (:raw opts) (-> parsed pprint)
      (:ast opts) (-> parsed transform pprint)
      (:csl opts) (-> parsed transform csl/gen-code print))))

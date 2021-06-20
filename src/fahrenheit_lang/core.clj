(ns fahrenheit-lang.core
  (:gen-class)
  (:require
   [clojure.edn :as edn]
   [clojure.pprint :refer [pprint]]
   [clojure.tools.cli :refer [parse-opts]]
   [fahrenheit-lang.transform :refer [transform]]
   [fahrenheit-lang.csl :as csl]))

(def cli-options
  [["-f" "--file FILE" "Path to *.fahr file"]
   [nil "--ast" "Parse *.fahr file and print preprocessed AST to standard output."
    :default false]
   [nil "--csl" "Generate CSL and print to standard output."
    :default true]])

(defn slurp-edn [file]
  (edn/read-string (slurp file)))

(defn -main [& args]
  (let [{opts :options} (parse-opts args cli-options)
        parsed (slurp-edn (:file opts))]
    (cond
      (:ast opts) (-> parsed transform pprint)
      (:csl opts) (-> parsed transform csl/gen-code print))))

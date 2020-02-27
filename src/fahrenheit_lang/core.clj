(ns fahrenheit-lang.core
  (:gen-class)
  (:require
   [clojure.pprint :as pp]
   [clojure.tools.cli :refer [parse-opts]]
   [fahrenheit-lang.parser :refer [parse]]
   [fahrenheit-lang.transform :as ft]
   [fahrenheit-lang.csl :as csl]))

(def cli-options
  [["-f" "--file FILE" "*.fahr file"]])

(defn -main
  [& args]
  (let [{opts :options} (parse-opts args cli-options)]
    (as-> (:file opts) x
          (slurp x)
          (parse x)
          (do (pp/pprint x) x)
          (ft/transform x)
          (do (pp/pprint x) x)
          (csl/gen-code x)
          (print x))))

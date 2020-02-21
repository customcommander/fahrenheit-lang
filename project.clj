(defproject fahrenheit-lang "0.1.0-SNAPSHOT"
  :description "domain-specific language for citation styles"
  :url "https://github.com/customcommander/fahrenheit-lang"
  :license {:name "MIT"
            :url "https://github.com/customcommander/fahrenheit-lang/blob/master/LICENSE"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/data.xml "0.0.8"]
                 [org.clojure/tools.cli "0.4.2"]
                 [instaparse "1.4.10"]]
  :main ^:skip-aot fahrenheit-lang.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
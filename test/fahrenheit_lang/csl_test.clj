(ns fahrenheit-lang.csl-test
  (:require [fahrenheit-lang.csl :as sut]
            [fahrenheit-lang.transform :as t]
            [clojure.spec.alpha :as s]
            [clojure.test :as test]))

(test/deftest about-title
  (letfn [(run [input]
            (sut/info :title (s/conform ::t/title input)))]

    (test/is (= (run "foo")
                [:title "foo"]))

    (test/is (= (run [:en "foo"])
                [:title {:xml:lang "en"} "foo"]))))

(ns fahrenheit-lang.transpiler-test
  (:require [fahrenheit-lang.transpiler :as sut]
            [clojure.spec.alpha :as s]
            [clojure.test :as t]))

(t/deftest print-smoke-tests
  (letfn [(print->csl [input]
            (sut/print->csl (s/conform ::sut/print input)))]

    (t/is (= (print->csl [:print "foo"])
             [:text {:value "foo"}]))

    (t/is (= (print->csl [:print :var/title])
             [:text {:variable "title"}]))
             
    (t/is (= (print->csl [:print :var/volume])
             [:number {:variable "volume"}]))

    (t/is (= (print->csl [:print :var/issued])
             [:date {:variable "issued"}]))

    (t/is (= (print->csl [:print :var/author :var/editor])
             [:names {:variable "author editor"}]))

    (t/is (= (print->csl [:print :term/no-date])
             [:text {:term "no-date"}]))

    (t/is (= (print->csl [:print :label/page])
             [:label {:variable "page"}]))

    (t/is (= (print->csl [:print 'my-macro])
             [:text {:macro "my-macro"}]))))

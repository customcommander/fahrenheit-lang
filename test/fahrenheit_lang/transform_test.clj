(ns fahrenheit-lang.transform-test
  (:require
   [fahrenheit-lang.transform :as sut]
   [fahrenheit-lang.test-helpers :refer [defspec-test]]))

(defspec-test ast-printxx `sut/ast-print)

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

(test/deftest about-authors
  (letfn [(run [input]
            (sut/info :authors (s/conform ::t/authors input)))]

    (test/is (= (run ["jane" {:name "joe" :email "joe@example.com"}])
                [[:author [:name "jane"]]
                 [:author [:name "joe"] [:email "joe@example.com"]]]))))

(test/deftest about-contributors
  (letfn [(run [input]
            (sut/info :contributors (s/conform ::t/contributors input)))]

    (test/is (= (run ["jane" {:name "joe" :email "joe@example.com"}])
                [[:contributor [:name "jane"]]
                 [:contributor [:name "joe"] [:email "joe@example.com"]]]))))

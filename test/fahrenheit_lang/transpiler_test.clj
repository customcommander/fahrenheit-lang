(ns fahrenheit-lang.transpiler-test
  (:require [fahrenheit-lang.transpiler :as sut]
            [clojure.spec.alpha :as s]
            [clojure.test :as t]))

(t/deftest print-smoke-tests
  (letfn [(print->csl [input]
            (sut/print->csl (s/conform ::sut/print input)))]

    (t/is (= (print->csl [:print {:format ["(" ")" :case/upper]} "foo"])
             [:text {:value "foo"
                     :prefix "("
                     :suffix ")"
                     :text-case "uppercase"}]))

    (t/is (= (print->csl [:print {:format ["(" ")" :case/upper]} :var/title])
             [:text {:variable "title"
                     :prefix "("
                     :suffix ")"
                     :text-case "uppercase"}]))
             
    (t/is (= (print->csl [:print {:format ["(" ")" :case/upper]} :var/volume])
             [:number {:variable "volume"
                       :prefix "("
                       :suffix ")"
                       :text-case "uppercase"}]))

    (t/is (= (print->csl [:print {:format ["(" ")" :case/upper]} :var/issued])
             [:date {:variable "issued"
                     :prefix "("
                     :suffix ")"
                     :text-case "uppercase"}]))

    (t/is (= (print->csl [:print {:format ["(" ")" :case/upper]} :var/author :var/editor])
             [:names {:variable "author editor"
                      :prefix "("
                      :suffix ")"
                      :text-case "uppercase"}]))

    (t/is (= (print->csl [:print {:format ["(" ")" :case/upper]} :term/no-date])
             [:text {:term "no-date"
                     :prefix "("
                     :suffix ")"
                     :text-case "uppercase"}]))

    (t/is (= (print->csl [:print {:format ["(" ")" :case/upper]} :label/page])
             [:label {:variable "page"
                      :prefix "("
                      :suffix ")"
                      :text-case "uppercase"}]))

    (t/is (= (print->csl [:print {:format ["(" ")" :case/upper]} 'my-macro])
             [:text {:macro "my-macro"
                     :prefix "("
                     :suffix ")"
                     :text-case "uppercase"}]))

    ;; make sure options remain optional
    (t/are [cmd csl] (= csl (print->csl cmd))
           [:print "foo"        ] [:text {:value "foo"}        ]
           [:print :var/title   ] [:text {:variable "title"}   ]
           [:print :var/volume  ] [:number {:variable "volume"}]
           [:print :var/issued  ] [:date {:variable "issued"}  ]
           [:print :var/author  ] [:names {:variable "author"} ]
           [:print :term/no-date] [:text {:term "no-date"}     ]
           [:print :label/page  ] [:label {:variable "page"}   ]
           [:print 'my-macro    ] [:text {:macro "my-macro"}   ])))

(t/deftest common-attributes
  (letfn [(format->csl
            [fmt]
            (sut/format->csl (s/conform ::sut/format fmt)))]

    (t/are [fmt csl-attrs] (= csl-attrs (format->csl fmt))
           [] nil
           ["("] {:prefix "("}
           ["(" ")"] {:prefix "(" :suffix ")"}
           ['_ '_ ","] {:delimiter ","}
           ["(" ")" ","] {:prefix "(" :suffix ")" :delimiter ","}
           ['_ '_ '_] nil
           ["("
            ")"
            ","
            :case/upper
            :format/italic
            :display/block]
              {:delimiter ","
               :prefix "("
               :suffix ")"
               :text-case "uppercase"
               :font-style "italic"
               :display "block"})
                             
    (t/is (= nil (format->csl [:foo :bar]))
          "ignore unknown keywords")))

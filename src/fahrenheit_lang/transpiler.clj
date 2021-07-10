;;; Copyright (c) 2021 Julien Gonzalez <hello@spinjs.com>

(ns fahrenheit-lang.transpiler
  (:require [clojure.spec.alpha :as s]
            [clojure.core.match :as m]
            [clojure.string :refer [join]]))

(derive :label/chapter-number            :print/label)  (derive :term/ordinal-57                 :print/term)
(derive :label/collection-number         :print/label)  (derive :term/ordinal-58                 :print/term)
(derive :label/edition                   :print/label)  (derive :term/ordinal-59                 :print/term)
(derive :label/issue                     :print/label)  (derive :term/ordinal-60                 :print/term)
(derive :label/locator                   :print/label)  (derive :term/ordinal-61                 :print/term)
(derive :label/number                    :print/label)  (derive :term/ordinal-62                 :print/term)
(derive :label/number-of-pages           :print/label)  (derive :term/ordinal-63                 :print/term)
(derive :label/number-of-volumes         :print/label)  (derive :term/ordinal-64                 :print/term)
(derive :label/page                      :print/label)  (derive :term/ordinal-65                 :print/term)
(derive :label/volume                    :print/label)  (derive :term/ordinal-66                 :print/term)
(derive :term/accessed                   :print/term)   (derive :term/ordinal-67                 :print/term)
(derive :term/ad                         :print/term)   (derive :term/ordinal-68                 :print/term)
(derive :term/and                        :print/term)   (derive :term/ordinal-69                 :print/term)
(derive :term/and-others                 :print/term)   (derive :term/ordinal-70                 :print/term)
(derive :term/anonymous                  :print/term)   (derive :term/ordinal-71                 :print/term)
(derive :term/at                         :print/term)   (derive :term/ordinal-72                 :print/term)
(derive :term/author                     :print/term)   (derive :term/ordinal-73                 :print/term)
(derive :term/available-at               :print/term)   (derive :term/ordinal-74                 :print/term)
(derive :term/bc                         :print/term)   (derive :term/ordinal-75                 :print/term)
(derive :term/book                       :print/term)   (derive :term/ordinal-76                 :print/term)
(derive :term/by                         :print/term)   (derive :term/ordinal-77                 :print/term)
(derive :term/chapter                    :print/term)   (derive :term/ordinal-78                 :print/term)
(derive :term/circa                      :print/term)   (derive :term/ordinal-79                 :print/term)
(derive :term/cited                      :print/term)   (derive :term/ordinal-80                 :print/term)
(derive :term/close-inner-quote          :print/term)   (derive :term/ordinal-81                 :print/term)
(derive :term/close-quote                :print/term)   (derive :term/ordinal-82                 :print/term)
(derive :term/collection-editor          :print/term)   (derive :term/ordinal-83                 :print/term)
(derive :term/column                     :print/term)   (derive :term/ordinal-84                 :print/term)
(derive :term/composer                   :print/term)   (derive :term/ordinal-85                 :print/term)
(derive :term/container-author           :print/term)   (derive :term/ordinal-86                 :print/term)
(derive :term/director                   :print/term)   (derive :term/ordinal-87                 :print/term)
(derive :term/edition                    :print/term)   (derive :term/ordinal-88                 :print/term)
(derive :term/editor                     :print/term)   (derive :term/ordinal-89                 :print/term)
(derive :term/editorial-director         :print/term)   (derive :term/ordinal-90                 :print/term)
(derive :term/editortranslator           :print/term)   (derive :term/ordinal-91                 :print/term)
(derive :term/et-al                      :print/term)   (derive :term/ordinal-92                 :print/term)
(derive :term/figure                     :print/term)   (derive :term/ordinal-93                 :print/term)
(derive :term/folio                      :print/term)   (derive :term/ordinal-94                 :print/term)
(derive :term/forthcoming                :print/term)   (derive :term/ordinal-95                 :print/term)
(derive :term/from                       :print/term)   (derive :term/ordinal-96                 :print/term)
(derive :term/ibid                       :print/term)   (derive :term/ordinal-97                 :print/term)
(derive :term/illustrator                :print/term)   (derive :term/ordinal-98                 :print/term)
(derive :term/in                         :print/term)   (derive :term/ordinal-99                 :print/term)
(derive :term/in-press                   :print/term)   (derive :term/original-author            :print/term)
(derive :term/internet                   :print/term)   (derive :term/page                       :print/term)
(derive :term/interview                  :print/term)   (derive :term/paragraph                  :print/term)
(derive :term/interviewer                :print/term)   (derive :term/part                       :print/term)
(derive :term/issue                      :print/term)   (derive :term/presented-at               :print/term)
(derive :term/letter                     :print/term)   (derive :term/recipient                  :print/term)
(derive :term/line                       :print/term)   (derive :term/reference                  :print/term)
(derive :term/long-ordinal-01            :print/term)   (derive :term/retrieved                  :print/term)
(derive :term/long-ordinal-02            :print/term)   (derive :term/reviewed-author            :print/term)
(derive :term/long-ordinal-03            :print/term)   (derive :term/scale                      :print/term)
(derive :term/long-ordinal-04            :print/term)   (derive :term/season-01                  :print/term)
(derive :term/long-ordinal-05            :print/term)   (derive :term/season-02                  :print/term)
(derive :term/long-ordinal-06            :print/term)   (derive :term/season-03                  :print/term)
(derive :term/long-ordinal-07            :print/term)   (derive :term/season-04                  :print/term)
(derive :term/long-ordinal-08            :print/term)   (derive :term/section                    :print/term)
(derive :term/long-ordinal-09            :print/term)   (derive :term/sub-verbo                  :print/term)
(derive :term/long-ordinal-10            :print/term)   (derive :term/translator                 :print/term)
(derive :term/month-01                   :print/term)   (derive :term/verse                      :print/term)
(derive :term/month-02                   :print/term)   (derive :term/version                    :print/term)
(derive :term/month-03                   :print/term)   (derive :term/volume                     :print/term)
(derive :term/month-04                   :print/term)   (derive :var/abstract                    :print/var)
(derive :term/month-05                   :print/term)   (derive :var/accessed                    :print/date)
(derive :term/month-06                   :print/term)   (derive :var/annote                      :print/var)
(derive :term/month-07                   :print/term)   (derive :var/archive-location            :print/var)
(derive :term/month-08                   :print/term)   (derive :var/archive-place               :print/var)
(derive :term/month-09                   :print/term)   (derive :var/archive                     :print/var)
(derive :term/month-10                   :print/term)   (derive :var/author                      :print/name)
(derive :term/month-11                   :print/term)   (derive :var/authority                   :print/var)
(derive :term/month-12                   :print/term)   (derive :var/call-number                 :print/var)
(derive :term/no-date                    :print/term)   (derive :var/chapter-number              :print/number)
(derive :term/note                       :print/term)   (derive :var/citation-label              :print/var)
(derive :term/online                     :print/term)   (derive :var/citation-number             :print/var)
(derive :term/open-inner-quote           :print/term)   (derive :var/collection-editor           :print/name)
(derive :term/open-quote                 :print/term)   (derive :var/collection-number           :print/number)
(derive :term/opus                       :print/term)   (derive :var/collection-title            :print/var)
(derive :term/ordinal                    :print/term)   (derive :var/composer                    :print/name)
(derive :term/ordinal-00                 :print/term)   (derive :var/container-author            :print/name)
(derive :term/ordinal-01                 :print/term)   (derive :var/container-title-short       :print/var)
(derive :term/ordinal-02                 :print/term)   (derive :var/container-title             :print/var)
(derive :term/ordinal-03                 :print/term)   (derive :var/container                   :print/date)
(derive :term/ordinal-04                 :print/term)   (derive :var/dimensions                  :print/var)
(derive :term/ordinal-05                 :print/term)   (derive :var/director                    :print/name)
(derive :term/ordinal-06                 :print/term)   (derive :var/doi                         :print/var)
(derive :term/ordinal-07                 :print/term)   (derive :var/edition                     :print/number)
(derive :term/ordinal-08                 :print/term)   (derive :var/editor                      :print/name)
(derive :term/ordinal-09                 :print/term)   (derive :var/editorial-director          :print/name)
(derive :term/ordinal-10                 :print/term)   (derive :var/event-date                  :print/date)
(derive :term/ordinal-11                 :print/term)   (derive :var/event-place                 :print/var)
(derive :term/ordinal-12                 :print/term)   (derive :var/event                       :print/var)
(derive :term/ordinal-13                 :print/term)   (derive :var/first-reference-note-number :print/var)
(derive :term/ordinal-14                 :print/term)   (derive :var/genre                       :print/var)
(derive :term/ordinal-15                 :print/term)   (derive :var/illustrator                 :print/name)
(derive :term/ordinal-16                 :print/term)   (derive :var/interviewer                 :print/name)
(derive :term/ordinal-17                 :print/term)   (derive :var/isbn                        :print/var)
(derive :term/ordinal-18                 :print/term)   (derive :var/issn                        :print/var)
(derive :term/ordinal-19                 :print/term)   (derive :var/issue                       :print/number)
(derive :term/ordinal-20                 :print/term)   (derive :var/issued                      :print/date)
(derive :term/ordinal-21                 :print/term)   (derive :var/jurisdiction                :print/var)
(derive :term/ordinal-22                 :print/term)   (derive :var/keyword                     :print/var)
(derive :term/ordinal-23                 :print/term)   (derive :var/locator                     :print/var)
(derive :term/ordinal-24                 :print/term)   (derive :var/medium                      :print/var)
(derive :term/ordinal-25                 :print/term)   (derive :var/note                        :print/var)
(derive :term/ordinal-26                 :print/term)   (derive :var/number-of-pages             :print/number)
(derive :term/ordinal-27                 :print/term)   (derive :var/number-of-volumes           :print/number)
(derive :term/ordinal-28                 :print/term)   (derive :var/number                      :print/number)
(derive :term/ordinal-29                 :print/term)   (derive :var/original-author             :print/name)
(derive :term/ordinal-30                 :print/term)   (derive :var/original-date               :print/date)
(derive :term/ordinal-31                 :print/term)   (derive :var/original-publisher-place    :print/var)
(derive :term/ordinal-32                 :print/term)   (derive :var/original-publisher          :print/var)
(derive :term/ordinal-33                 :print/term)   (derive :var/original-title              :print/var)
(derive :term/ordinal-34                 :print/term)   (derive :var/page-first                  :print/var)
(derive :term/ordinal-35                 :print/term)   (derive :var/page                        :print/var)
(derive :term/ordinal-36                 :print/term)   (derive :var/pmcid                       :print/var)
(derive :term/ordinal-37                 :print/term)   (derive :var/pmid                        :print/var)
(derive :term/ordinal-38                 :print/term)   (derive :var/publisher-place             :print/var)
(derive :term/ordinal-39                 :print/term)   (derive :var/publisher                   :print/var)
(derive :term/ordinal-40                 :print/term)   (derive :var/recipient                   :print/name)
(derive :term/ordinal-41                 :print/term)   (derive :var/references                  :print/var)
(derive :term/ordinal-42                 :print/term)   (derive :var/reviewed-author             :print/name)
(derive :term/ordinal-43                 :print/term)   (derive :var/reviewed-title              :print/var)
(derive :term/ordinal-44                 :print/term)   (derive :var/scale                       :print/var)
(derive :term/ordinal-45                 :print/term)   (derive :var/section                     :print/var)
(derive :term/ordinal-46                 :print/term)   (derive :var/source                      :print/var)
(derive :term/ordinal-47                 :print/term)   (derive :var/status                      :print/var)
(derive :term/ordinal-48                 :print/term)   (derive :var/submitted                   :print/date)
(derive :term/ordinal-49                 :print/term)   (derive :var/title-short                 :print/var)
(derive :term/ordinal-50                 :print/term)   (derive :var/title                       :print/var)
(derive :term/ordinal-51                 :print/term)   (derive :var/translator                  :print/name)
(derive :term/ordinal-52                 :print/term)   (derive :var/url                         :print/var)
(derive :term/ordinal-53                 :print/term)   (derive :var/version                     :print/var)
(derive :term/ordinal-54                 :print/term)   (derive :var/volume                      :print/number)
(derive :term/ordinal-55                 :print/term)   (derive :var/year-suffix                 :print/var)
(derive :term/ordinal-56                 :print/term)

(def flag->csl
  {:case/lower            [:text-case       "lowercase"       ]
   :case/upper            [:text-case       "uppercase"       ]
   :case/sentence         [:text-case       "sentence"        ]
   :case/capitalize-first [:text-case       "capitalize-first"]
   :case/capitalize-all   [:text-case       "capitalize-all"  ]
   :case/title            [:text-case       "title"           ]
   :format/italic         [:font-style      "italic"          ]
   :format/oblique        [:font-style      "oblique"         ]
   :format/small-caps     [:font-variant    "small-caps"      ]
   :format/bold           [:font-weight     "bold"            ]
   :format/light          [:font-weight     "light"           ]
   :format/underline      [:text-decoration "underline"       ]
   :format/sup            [:vertical-align  "superscript"     ]
   :format/sub            [:vertical-align  "subscript"       ]
   :display/block         [:display         "block"           ]
   :display/left          [:display         "left-margin"     ]
   :display/right         [:display         "right-inline"    ]
   :display/indent        [:display         "indent"          ]})

(s/def ::str-or-na
  (s/or :n-a #{'_}
        :str string?))

;; e.g. [_ "(" ")" :format/bold :case/upper]
;;      [:format/italic :display/indent :case/sentence]
(s/def ::format
  (s/cat :delim-affixes (s/* ::str-or-na)
         :flags (s/* keyword?)))

(s/def ::opts (s/keys :opt-un [::format]))

(s/def ::print
  (s/cat :cmd #{:print}
         :opts (s/? ::opts)
         :body (s/alt :str string?
                      :var #(isa? % :print/var)
                      :num #(isa? % :print/number)
                      :date #(isa? % :print/date)
                      :name (s/+ #(isa? % :print/name)) ; TODO: must accept sub commands as substitutes
                      :term #(isa? % :print/term)
                      :label #(isa? % :print/label)
                      :sym symbol?)))

(defn format->csl
  [{da :delim-affixes fl :flags}]
  (merge (when da
           (let [[p s d] da]
             (letfn [(extract
                       [[k v]]
                       (when-not (= k :n-a) v))]
               (merge (when-let [v (extract d)]
                        {:delimiter v})
                      (when-let [v (extract p)]
                        {:prefix v})
                      (when-let [v (extract s)]
                        {:suffix v})))))
         (when fl
           (let [m (into {} (map flag->csl fl))]
             (when-not (empty? m) m)))))

(defmulti print->csl #(-> % :body first))

(defmethod print->csl :str
  [{o :opts [_ s] :body}]
  [:text (merge {:value s}
                (format->csl (:format o)))])

(defmethod print->csl :var
  [{o :opts [_ qkw] :body}]
  [:text (merge {:variable (name qkw)}
                (format->csl (:format o)))])

(defmethod print->csl :num
  [{o :opts [_ qkw] :body}]
  [:number (merge {:variable (name qkw)}
                  (format->csl (:format o)))])

(defmethod print->csl :date
  [{o :opts [_ qkw] :body}]
  [:date (merge {:variable (name qkw)}
                (format->csl (:format o)))])

(defmethod print->csl :name
  [{o :opts [_ qkws] :body}]
  [:names (merge {:variable (join " " (map name qkws))}
                 (format->csl (:format o)))])

(defmethod print->csl :term
  [{o :opts [_ qkw] :body}]
  [:text (merge {:term (name qkw)}
                (format->csl (:format o)))])

(defmethod print->csl :label
  [{o :opts [_ qkw] :body}]
  [:label (merge {:variable (name qkw)}
                 (format->csl (:format o)))])

(defmethod print->csl :sym
  [{o :opts [_ sym] :body}]
  [:text (merge {:macro (name sym)}
                (format->csl (:format o)))])

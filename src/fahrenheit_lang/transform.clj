; Copyright (c) 2021 Julien Gonzalez
(ns fahrenheit-lang.transform
  (:require [clojure.spec.alpha :as s]))

(s/def ::lang #{:en})
(s/def ::str string?)
(s/def ::str-localised (s/or :str ::str
                             :strl (s/tuple ::lang ::str)))

(s/def ::id ::str)
(s/def ::title ::str-localised)
(s/def ::title-short ::str-localised)

(s/def ::about (s/keys :req-un [::id ::title]
                       :opt-un [::title-short]))

(s/def ::style (s/keys :req-un [::about]))

(defn transform [style]
  (s/conform ::style style))

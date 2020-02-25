(ns fahrenheit-lang.compiler.record
  (:require [clojure.string :as string]))

(defn strtail [s]
  (-> s rest string/join))

(defprotocol CSL
  (to-csl [r]))

(defrecord PrintVar [modifiers var]
  CSL
  (to-csl [this]
    [:text (assoc modifiers :variable (strtail var))]))

(defrecord PrintNumber [modifiers var]
  CSL
  (to-csl [this]
    [:number (assoc modifiers :variable (strtail var))]))

(defn map->Print [m]
  (condp = (:var m)
    "$title" (map->PrintVar m)
    "$chapter-number" (map->PrintNumber m)
    "$collection-number" (map->PrintNumber m)
    "$edition" (map->PrintNumber m)
    "$issue" (map->PrintNumber m)
    "$number" (map->PrintNumber m)
    "$number-of-pages" (map->PrintNumber m)
    "$number-of-volumes" (map->PrintNumber m)
    "$volume" (map->PrintNumber m)))
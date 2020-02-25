(ns fahrenheit-lang.compiler.record
  (:require [clojure.string :as string]))

(defprotocol CodeGen
  (to-csl [_]))

(defrecord PrintText [modifiers var]
  CodeGen
  (to-csl [_]
    [:text (assoc modifiers :variable var)]))

(defrecord PrintNumber [modifiers var]
  CodeGen
  (to-csl [_]
    [:number (assoc modifiers :variable var)]))

(defn map->Print [m]
  (cond
    (:var-txt m) (->PrintText (:modifiers m) (:var-txt m))
    (:var-num m) (->PrintNumber (:modifiers m) (:var-num m))))

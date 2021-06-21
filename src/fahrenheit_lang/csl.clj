(ns fahrenheit-lang.csl
  (:require [clojure.data.xml :as xml]
            [clojure.core.match :as m]))

(defn localised-node [node-name ast]
  (m/match [ast]
    [[:str v]] [node-name v]
    [[:strl [l v]]] [node-name {:xml:lang (name l)} v]))

; ast conformed as per the `person` specifiction
; e.g. "john" -> [:str "john"]
; e.g. {:name "john"} -> [:map {:name "john"}]
(defn person [ast]
  (m/match [ast]
    [[:str v]] [[:name v]]
    [[:map v]] (seq v)))

(defmulti info
  (fn [k v]
    (when v k)))

(defmethod info :id [_ v]
  [:id v])

(defmethod info :title [_ ast]
  (localised-node :title ast))

(defmethod info :title-short [_ ast]
  (localised-node :title-short ast))

; ast conformed as per the `persons` spec
; e.g. ["john" {:name "jane"}] -> [[:str "john"] [:map {:name "jane"}]]
(defmethod info :authors [_ ast]
  (map #(identity `[:author ~@(person %)]) ast))

; ast conformed as per the `persons` spec
; e.g. ["john" {:name "jane"}] -> [[:str "john"] [:map {:name "jane"}]]
(defmethod info :contributors [_ ast]
  (map #(identity `[:contributor ~@(person %)] ) ast))

(defmethod info :default [_ _] nil)

(defn gen-csl [style]
  [:style {:version "1.0"}
    (let [about (:about style)]
      `[:info ~(info :id (:id about))
              ~(info :title (:title about))
              ~(info :title-short (:title-short about))
             ~@(info :authors (:authors about))
             ~@(info :contributors (:contributors about))])])

(defn gen-code [style]
  (-> style
      gen-csl
      xml/sexp-as-element
      xml/indent-str))

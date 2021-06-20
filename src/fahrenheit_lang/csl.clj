(ns fahrenheit-lang.csl
  (:require [clojure.data.xml :as xml]
            [clojure.core.match :as m]))

(defn localised-node [node-name ast]
  (m/match [ast]
    [[:str v]] [node-name v]
    [[:strl [l v]]] [node-name {:xml:lang (name l)} v]))

(defmulti info
  (fn [k v]
    (when v k)))

(defmethod info :id [_ v]
  [:id v])

(defmethod info :title [_ ast]
  (localised-node :title ast))

(defmethod info :title-short [_ ast]
  (localised-node :title-short ast))

(defmethod info :default [_ _] nil)

(defn gen-code [style]
  (xml/indent-str
    (xml/sexp-as-element
      [:style {:version "1.0"}
        (let [about (:about style)]
          [:info (info :id (:id about))
                 (info :title (:title about))
                 (info :title-short (:title-short about))])])))

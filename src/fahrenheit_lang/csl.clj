(ns fahrenheit-lang.csl
  (:require
   [clojure.data.xml :as xml]
   [clojure.zip :as zip]
   [clojure.set :as st]))

; based on https://spdx.org/licenses/
(def license-map
  {:CC-BY-SA-3.0
    {:uri "https://creativecommons.org/licenses/by-sa/3.0/"
     :fullname "Creative Commons Attribution Share Alike 3.0 Unported"}})

; default meta to csl code generator
(defn meta->csl [loc]
  (let [up (zip/up loc)
        [key args] (zip/children up)
        lang (st/rename-keys (dissoc args :value) {:lang :xml:lang})
        content (:value args)]
    (zip/right (zip/replace up
                            [key lang content]))))

(defn meta-link->csl [loc]
  (let [[kind args] (zip/node (zip/up loc))
        new-args (assoc (st/rename-keys args {:value :href :lang :xml:lang})
                        :rel
                        (kind {:url "self"
                               :documentation "documentation"
                               :template "template"}))]
    (-> loc
        (zip/up)
        (zip/replace [:link new-args]))))

(defmulti ->csl zip/node)

(defmethod ->csl :default [loc] loc)

(defmethod ->csl :program [loc]
  (zip/replace loc :style))

(defmethod ->csl :metadata [loc]
  (zip/replace loc :info))

(defmethod ->csl :title [loc] (meta->csl loc))
(defmethod ->csl :title-short [loc] (meta->csl loc))
(defmethod ->csl :id [loc] (meta->csl loc))
(defmethod ->csl :url [loc] (meta-link->csl loc))
(defmethod ->csl :documentation [loc] (meta-link->csl loc))
(defmethod ->csl :template [loc] (meta-link->csl loc))
(defmethod ->csl :issn [loc] (meta->csl loc))
(defmethod ->csl :eissn [loc] (meta->csl loc))
(defmethod ->csl :issnl [loc] (meta->csl loc))
(defmethod ->csl :summary [loc] (meta->csl loc))
(defmethod ->csl :published [loc] (meta->csl loc))
(defmethod ->csl :updated [loc] (meta->csl loc))

(defmethod ->csl :citation-format [loc]
  (-> loc
      (zip/replace :category)
      (zip/next)
      (zip/edit #(st/rename-keys % {:value :citation-format}))))

(defmethod ->csl :field [loc]
  (-> loc
      (zip/replace :category)
      (zip/next)
      (zip/edit #(st/rename-keys % {:value :field}))))

(defmethod ->csl :license [loc]
  (let [args (zip/node (zip/next loc))
        lang (:lang args)
        id (keyword (:value args))
        license (id license-map)]
    (-> loc
        (zip/up)
        (zip/replace [:rights
                        (cond
                          (and lang license) {:license (:uri license) :xml:lang lang}
                          (some license)     {:license (:uri license)}
                          (some lang)        {:xml:lang lang}
                          :else              {})
                        (if license
                          (:fullname license)
                          (:value args))]))))

(defmethod ->csl :person [loc]
  (let [args (zip/node (zip/next loc))]
    (-> loc
        (zip/up)
        (zip/replace [(:kind args)
                        [:name (:name args)]
                        (when-let [email (:email args)]
                          [:email email])
                        (when-let [url (:url args)]
                          [:uri url])])
        (zip/next))))

(defmethod ->csl :foreach-cite [loc]
  (zip/replace loc :layout))

(defmethod ->csl :print [loc]
  (let [args (zip/node (zip/next loc))
        node ((:kind args) {:text :text
                            :term :text
                            :number :number})
        var ((:kind args) {:text :variable
                           :number :variable
                           :term :term})]
    (-> loc
        (zip/replace node)
        (zip/next)
        (zip/edit #(-> %
                       (dissoc :kind)
                       (st/rename-keys {:var var}))))))

(defn gen-code [ast]
  (loop [loc (zip/vector-zip ast)]
    (if (zip/end? loc)
      (-> loc
          zip/root
          xml/sexp-as-element
          xml/indent-str)
      (recur (->csl (zip/next loc))))))

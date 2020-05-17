; The purpose of this program is to transform the original AST
; into something easier for code generators to consume.

(ns fahrenheit-lang.transform
  (:require
   [clojure.zip :as zip]
   [clojure.string :as string]
   [clojure.spec.alpha :as s]))

(defn group-date-formats [args]
  (loop [in args
         out [[] []]]
    (if (empty? in)
      (if (empty? (second out))
        (first out)
        (cons [:date-format (second out)] (first out)))
      (recur (rest in)
             (let [arg (first in)
                   arg-name (first arg)
                   [a b] out]
              (if (or (= arg-name :date-format-year)
                      (= arg-name :date-format-month)
                      (= arg-name :date-format-day))
                [a (conj b arg)]
                [(conj a arg) b]))))))

(defmulti transform-arg first)

; [:foo "bar"] -> {:foo "bar"}
(defmethod transform-arg :default [[k v]] {k v})

(defmethod transform-arg :date-format [[k v]]
  (letfn [
          ; add some default values to given map
          (with-defaults [m]
            (if (some? (:format m))
              m
              (assoc m :format (condp = (:part m)
                                  :year  :n
                                  :month :n
                                  :day   :n))))

          ; some things should be renamed
          (rename [x]
            (condp = x
              :date-format-year  :year
              :date-format-month :month
              :date-format-day   :day
              :date-prefix       :prefix
              :date-suffix       :suffix
              :date-formatter    :format
              "n"                :n
                                 x))

          ; map a vector of specs into a map of specs that is easier to consume
          ;
          ; from:
          ;
          ; [:date-format-day [:date-prefix "("]
          ;                   [:date-formatter "n"]
          ;                   [:date-suffix "("]]
          ;
          ; to:
          ;
          ; {:prefix "("
          ;  :suffix ")"
          ;  :part :year
          ;  :format :n}
          ;
          (mapper [[fk & fv]]
            (let [parts (mapcat #(map rename %) `[[:part ~fk] ~@fv])]
              (with-defaults (apply assoc `[{} ~@parts]))))]

    {:date-format (mapv mapper v)}))

(defmulti transform-ast zip/node)

(defmethod transform-ast :default [loc] loc)

(defmethod transform-ast :program [loc]
  (let [siblings (zip/rights loc)]
    (letfn [(take-program [k ast]
              (filter #(= k (first %)) ast))]
      (-> loc
          (zip/up)
          (zip/replace `[:program
                          ~@(take-program :about siblings)
                          [:macros ~@(take-program :macro siblings)]
                          ~@(take-program :citation siblings)])
          (zip/next)))))

(defmethod transform-ast :about [loc]
  (let [metadata (zip/rights loc)]
    (-> loc
        (zip/up)
        (zip/replace `[:metadata ~@metadata]))))

(defmethod transform-ast :meta-statement [loc]
  (let [next-loc (zip/next loc)
        [head & tail] (zip/node next-loc)]
    (condp = head
      :person-statement :>> (fn [_] (transform-ast next-loc))
      :field-statement  :>> (fn [_] (transform-ast next-loc))
      ; default ast transformation for all other meta fields
      (-> loc
          (zip/up)
          (zip/replace [head (zipmap [:value :lang] tail)])
          (zip/next)))))

(defmethod transform-ast :person-statement [loc]
  (let [[kind fullname & details] (zip/node (zip/next loc))
        args (for [[k v lang] (concat [[:kind kind] [:name fullname]] details)]
              (if lang [[k v] [(keyword (str (name k) "-lang")) lang]]
                       [k v]))]
    (-> loc
        (zip/up)
        (zip/up)
        (zip/replace [:person (apply hash-map (flatten args))])
        (zip/next))))

(defmethod transform-ast :field-statement [loc]
  (let [value (zip/node (zip/next loc))]
    (loop [fields (map string/trim (string/split value #","))
           up (zip/up (zip/up loc))]
      (if (empty? fields)
        (zip/next (zip/remove up))
        (recur (rest fields)
               (zip/insert-left up [:field {:value (first fields)}]))))))

(defmethod transform-ast :citation [loc]
  (let [siblings (zip/rights loc)]
    (-> loc
        (zip/up)
        (zip/replace `[:citation [:foreach-cite {} ~@siblings]])
        (zip/next))))

(defmethod transform-ast :print [loc]
  (let [[head & tail] (zip/node (zip/next loc))]
    (-> loc
        (zip/up)
        (zip/replace `[~head {} ~@tail]))))

(defn transform-args [loc]
  (let [args (zip/rights loc)]
    (-> loc
        (zip/up)
        (zip/remove)
        (zip/replace (apply merge `[~@(map transform-arg (group-date-formats args))])))))

(defmethod transform-ast :args [loc] (transform-args loc))
(defmethod transform-ast :date-args [loc] (transform-args loc))

(defn transform-var [loc]
  (let [id (zip/node (zip/next loc))]
    (-> loc
        (zip/up)
        (zip/remove)
        (zip/edit #(merge % {:variable id})))))

(defmethod transform-ast :var-txt [loc] (transform-var loc))
(defmethod transform-ast :var-num [loc] (transform-var loc))
(defmethod transform-ast :var-term [loc] (transform-var loc))
(defmethod transform-ast :var-date [loc] (transform-var loc))
(defmethod transform-ast :var-macro [loc] (transform-var loc))

(defn transform [ast]
  (loop [loc (zip/vector-zip ast)]
    (if (zip/end? loc)
      (zip/root loc)
      (recur (transform-ast (zip/next loc))))))

;; specs

(s/def ::var-kind
  #{:var-txt
    :var-num
    :var-term
    :var-date
    :var-macro})

(s/def ::ast-print
  (s/cat :kind ::var-kind :names (s/+ string?)))

;; spec'ed functions

(defn ast-print [_ & names]
  {:variable (if (= 1 (count names))
                 (first names)
                 (vec names))})

(s/fdef ast-print
  :args ::ast-print
  :ret map?
  :fn #(or (= (:ret %) {:variable (-> % :args :names first)})
           (= (:ret %) {:variable (-> % :args :names)})))

; The purpose of this program is to transform the original AST
; into something easier for code generators to consume.

(ns fahrenheit-lang.transform
  (:require
   [clojure.zip :as zip]
   [clojure.string :as string]))

(defmulti transform-ast zip/node)

(defmethod transform-ast :default [loc] loc)

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

(defmethod transform-ast :modifiers [loc]
  (let [args (zip/rights loc)]
    (-> loc
        (zip/up)
        (zip/remove)
        (zip/edit #(merge % (into {} args))))))

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

(defn transform [ast]
  (loop [loc (zip/vector-zip ast)]
    (if (zip/end? loc)
      (zip/root loc)
      (recur (transform-ast (zip/next loc))))))

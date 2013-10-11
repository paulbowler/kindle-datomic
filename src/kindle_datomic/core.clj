(ns kindle-datomic.core
  (:require [datomic.api :as d]))

(def conn nil)

(defn add-title [book-title]
  @(d/transact conn [{:db/id (d/tempid :db.part/user)
                      :book/title book-title}]))

(defn find-all-book-titles []
  (d/q '[:find ?book-title
         :where [_ :book/title ?book-title]]
       (d/db conn)))


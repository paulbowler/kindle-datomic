(ns kindle-datomic.core
  (:require [datomic.api :as d]))

(def conn nil)

(defn add-title [book-title]
  @(d/transact conn [{:db/id (d/tempid :db.part/user)
                      :book/title book-title}]))

(defn get-title-id [book-title]
  (ffirst (d/q '[:find ?eid
         :in $ ?book-title
         :where [?eid :book/title ?book-title]]
       (d/db conn)
       book-title)))

(defn get-author-id [author-name]
  (ffirst (d/q '[:find ?eid
         :in $ ?author-name
         :where [?eid :author/name ?author-name]]
       (d/db conn)
       author-name)))

(defn add-author-to-title [author-name book-title]
  (let [author-id (d/tempid :db.part/user)]
    @(d/transact conn [{:db/id author-id
                      :author/name author-name}
                       {:db/id (get-title-id book-title)
                      :book/authors author-id}])))

(defn find-all-book-titles []
  (d/q '[:find ?book-title
         :where [_ :book/title ?book-title]]
       (d/db conn)))

(defn find-titles-for-author [author-name]
  (d/q '[:find ?title
         :where
         [?e :book/authors ?author-name]
         [?e :book/title ?title]]
       (d/db conn)
       author-name))

(defn find-all-authors []
  (d/q '[:find ?author-name
         :where [_ :author/name ?author-name]]
       (d/db conn)))

(defn add-clipping-to-title [clipping-text book-title]
  (let [clipping-id (d/tempid :db.part/user)]
    @(d/transact conn [{:db/id clipping-id
                      :clipping/text clipping-text}
                       {:db/id (get-title-id book-title)
                      :book/clippings clipping-id}])))

(defn find-all-clippings []
  (d/q '[:find ?clipping-text
         :where [_ :clipping/text ?clipping-text]]
       (d/db conn)))

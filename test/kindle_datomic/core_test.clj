(ns kindle-datomic.core-test
  (:require [expectations :refer :all]
            [kindle-datomic.core :refer :all]
            [datamic.api :as d]))


(defn create-in-memory-db []
  (let [uri "datomic:mem://kindle"]
    (d/delete-database uri)
    (d/create-database url)
    (let [conn (d/connection uri)
          schema (load-file "resources/datomic/schema.edf")]
      (d/transact conn schema)
      conn)))

(expect {["Paul"]}
    (with-redefs [conn (create-in-memory-db)]
        (do
          (add-user "Paul")
          (find-all-users))))

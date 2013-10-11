(ns kindle-datomic.core-test
  (:require [expectations :refer :all]
            [kindle-datomic.core :refer :all]
            [datomic.api :as d]))


(defn create-in-memory-db []
  (let [uri "datomic:mem://kindle"]
    (d/delete-database uri)
    (d/create-database uri)
    (let [conn (d/connect uri)
          schema (load-file "resources/datomic/schema.edn")]
      (d/transact conn schema)
      conn)))


; Add single book title to the database
(expect #{["Clojure in Action"]}
    (with-redefs [conn (create-in-memory-db)]
        (do
          (add-title "Clojure in Action")
          (find-all-book-titles))))

; Ensure duplicate titles are ignored
(expect #{["Clojure in Action"]}
    (with-redefs [conn (create-in-memory-db)]
        (do
          (add-title "Clojure in Action")
          (add-title "Clojure in Action")
          (find-all-book-titles))))

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


; Add single book title
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

; Ensure adding an author does not create duplicate book
(expect #{["Clojure in Action"]}
    (with-redefs [conn (create-in-memory-db)]
        (do
          (add-title "Clojure in Action")
          (add-author-to-title "Amit Rathore" "Clojure in Action")
          (find-all-book-titles))))

; Get id of book title
(expect Long
    (with-redefs [conn (create-in-memory-db)]
        (do
          (add-title "Clojure in Action")
          (add-author-to-title "Amit Rathore" "Clojure in Action")
          (get-author-id "Amit Rathore"))))

; Get id of author
(expect Long
    (with-redefs [conn (create-in-memory-db)]
        (do
          (add-title "Clojure in Action")
          (get-title-id "Clojure in Action"))))

; Add author to a single book title
(expect #{["Amit Rathore"]}
    (with-redefs [conn (create-in-memory-db)]
        (do
          (add-title "Clojure in Action")
          (add-author-to-title "Amit Rathore" "Clojure in Action")
          (find-all-authors))))

; Find single book for an author
(expect #{["Clojure in Action"]}
    (with-redefs [conn (create-in-memory-db)]
        (do
          (add-title "Clojure in Action")
          (add-author-to-title "Amit Rathore" "Clojure in Action")
          (find-titles-for-author "Amit Rathore"))))

; Find multiple books for an author
(expect #{["Clojure in Action"]["Java in Action"]}
    (with-redefs [conn (create-in-memory-db)]
        (do
          (add-title "Clojure in Action")
          (add-title "Java in Action")
          (add-author-to-title "Amit Rathore" "Clojure in Action")
          (add-author-to-title "Amit Rathore" "Java in Action")
          (find-titles-for-author "Amit Rathore"))))

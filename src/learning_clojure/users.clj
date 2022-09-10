(ns learning-clojure.users
  (:require [learning-clojure.core :refer :all]
            [clojure.string :as str]))

(defn create-user [name b-date-str]
  {:name name :birth-date (date b-date-str)})

(defn csv-line-to-user [line]
  {:pre [(not (str/includes? line " ,"))
         (not (str/includes? line ", "))]}
  (let [split-line (str/split line #",")]
    (create-user (first split-line) (second split-line))))

(defn get-users-from-csv [file-name]
  (map
    csv-line-to-user (subvec (get-lines file-name) 1)))

(defn mock-users []
  [(create-user "Nika" "1998-12-24")
   (create-user "Nino" "1990-10-22")
   (create-user "Jesus Jr" "1997-12-25")
   (create-user "Jesus" "0000-12-25")
   (create-user "Somebody" "2010-12-24")])

(defn csv-users [] (get-users-from-csv "users.csv"))

(ns learning-clojure.core
  (:require [clojure.java.io :as io])
  (:import (java.time LocalDate))
  (:import (java.lang IllegalStateException))
  )

(defn date [date-str]
  (let [java-date (LocalDate/parse date-str)]
    {:y (.getYear java-date)
     :m (.getMonthValue java-date)
     :d (.getDayOfMonth java-date)}
    ))

(defn get-lines [file-path]
  (with-open [file (io/reader (io/input-stream (io/resource file-path)))]
    (vec (line-seq file))))

(defn check-state [boolean-func ^String message]
  (if (= true (boolean-func))
    true (throw (IllegalStateException. message))))
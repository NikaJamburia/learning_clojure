(ns learning-clojure.core-test
  (:require [clojure.test :refer :all]
            [learning-clojure.core :refer :all])
  (:import (java.time.format DateTimeParseException)))

(deftest date-test
  (testing "correctly parses date"
    (are [x y] (= x y)
               (:y (date "2022-09-11")) 2022
               (:m (date "2022-09-11")) 9
               (:d (date "2022-09-11")) 11))

  (testing "throws exception on incorrect date"
    (is (thrown? DateTimeParseException (date "2022-100-1"))))
  )

(deftest get-lines-test
  (testing "Gets lines from file"
    (let [result (get-lines "test-users.csv")]
      (are [x y] (= x y)
                 (count result) 3
                 (first result) "name,birth-date"
                 (second result) "Nika,2008-11-18"
                 (get result 2) "Bika,1996-12-19"))
    ))

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

(deftest check-state-test
  (testing "Throws IllegalStateException with given message when expression resolves to false"
    (are [call msg] (thrown-with-msg? IllegalStateException msg (call))
                    #(check-state (fn [] (= 1 2)) "1 is not equal to 2") #"1 is not equal to 2"
                    #(check-state (fn [] (contains? [1 2 3] 4)) "Does not contain") #"Does not contain"
                    ))

  (testing "Does not throw error and just returns true when given expression resolves to true"
        (are [x] (= true x)
                 (check-state #(= 1 1) "Ragaca1")
                 (check-state #(= "a" "a") "Ragaca2")
                 (check-state #(contains? [1 2 3] 2) "Ragaca3")
                 ))
  )
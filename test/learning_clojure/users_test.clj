(ns learning-clojure.users-test
  (:require [clojure.test :refer :all])
  (:require [learning-clojure.users :refer :all]
            [learning-clojure.core :refer :all]))

(deftest mock-users-test
  (testing "Returns vector of mocked users"
    (is (= (count (mock-users))) 5)))


(deftest csv-line-to-user-test
  (testing "Converts given csv line to a map representing user"
    (is
      (=
        (csv-line-to-user "Nika Jamburia,1998-12-24") {:name "Nika Jamburia" :birth-date (date "1998-12-24")})))
  (testing "Throws exception if line is incorrect"
    (are [err x] (thrown? err x)
             Exception (csv-line-to-user "Nika1998-12-24")
             Exception (csv-line-to-user "Nika,1998-2-24")
             AssertionError (csv-line-to-user "Nika, 1998-12-24")
             AssertionError (csv-line-to-user "Nika ,1998-12-24"))
  ))

(deftest get-users-from-csv-test
  (testing "Takes all users from csv and maps them"
    (let [result (get-users-from-csv "test-users.csv")]
      (are [x y] (= x y)
                 (count result) 2
                 (first result) {:name "Nika" :birth-date (date "2008-11-18")}
                 (second result) {:name "Bika" :birth-date (date "1996-12-19")})
      )))
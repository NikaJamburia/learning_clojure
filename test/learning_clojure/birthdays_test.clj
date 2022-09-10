(ns learning-clojure.birthdays-test
  (:require [clojure.test :refer :all]
            [learning-clojure.birthdays :refer :all]
            [learning-clojure.core :refer :all]))

(defn- born-on [date-str]
  {:birth-date (date date-str)})

(deftest filter-by-birth-month-and-day-test
  (let [users [ {:name "Nika" :birth-date (date "1998-12-24")} {:name "Jesus" :birth-date (date "1998-12-25")} ]]
      (testing "Gets users whose birthdate is on given dates day and month"
        (are [x y] (= x y)
                   (:name (first (filter-by-birth-month-and-day users (date "2022-12-24")))) "Nika"
                   (:name (first (filter-by-birth-month-and-day users (date "2022-12-25")))) "Jesus"))
      (testing "Returns empty collection if no such users found"
        (is (empty? (filter-by-birth-month-and-day users "2022-12-26"))))
    )
  )

(deftest calc-age-test
  (testing "subtracts users birth year from given date year and takes into consideration birth month and date"
    (are [x y] (= x y)
               (calc-age (born-on "1998-12-24") (date "2022-12-23")) 23
               (calc-age (born-on "1998-12-24") (date "2022-12-24")) 24
               (calc-age (born-on "1998-12-24") (date "2022-12-25")) 24
               (calc-age (born-on "1998-12-24") (date "2023-01-01")) 24
               (calc-age (born-on "1998-12-24") (date "2021-01-01")) 22
               (calc-age (born-on "1998-11-24") (date "2022-12-24")) 24
               (calc-age (born-on "1998-11-24") (date "2022-10-24")) 23)
    ))

(deftest generate-birthday-msg-test
  (testing "Inserts users name and calculated age into message"
    (is (=
          (generate-birthday-msg {:name "Nika", :birth-date (date "1998-12-24")} (date "2022-12-24"))
          "Happy Birthday Nika! You are now 24 years old")))
  )

(deftest collect-birthday-messages-test
  (testing "Filters users with birthday and generated messages for them"
    (let [
          nika {:name "Nika" :birth-date (date "1998-12-24")}
          user {:name "Vigaca" :birth-date (date "2000-12-24")}
          nino {:name "Nino" :birth-date (date "1991-10-22")}
          result (collect-birthday-messages (date "2022-12-24") (fn [] [nika user nino]))
          ]
      (are [x y] (= x y)
                 (count result) 2
                 (:user (first result)) nika
                 (:message (first result)) "Happy Birthday Nika! You are now 24 years old"
                 (:user (second result)) user
                 (:message (second result)) "Happy Birthday Vigaca! You are now 22 years old"
                 ))
    ))
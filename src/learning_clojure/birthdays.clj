(ns learning-clojure.birthdays
  (:require [learning-clojure.core :refer :all]))

(defn filter-by-birth-month-and-day
  [users date]
  (filter
    #(and
       (= (:m date) (:m (:birth-date %)))
       (= (:d date) (:d (:birth-date %))))
    users)
  )

(defn calc-age [user on-date]
  (let [birth-date (:birth-date user)
        year-diff (- (:y on-date) (:y birth-date))]
    (cond
      (< (:m on-date) (:m birth-date)) (- year-diff 1)
      (> (:m on-date) (:m birth-date)) year-diff
      (< (:d on-date) (:d birth-date)) (- year-diff 1)
      :else year-diff))
  )

(defn generate-birthday-msg [user on-date]
  (str "Happy Birthday " (:name user) "! You are now " (calc-age user on-date) " years old"))



(defn collect-birthday-messages [on-date users-fun]
  (let [users (users-fun)]
    (map
      (fn [x] {:user x :message (generate-birthday-msg x on-date)})
      (filter-by-birth-month-and-day users on-date))))

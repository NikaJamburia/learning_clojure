(ns learning-clojure.ttt.tic-tac-toe-core-test
  (:require [clojure.test :refer :all]
            [learning-clojure.ttt.tic-tac-toe-core :refer :all]))

(deftest coordinates
  (testing "tic tac toe coordinates can't be less than 0 or more then 2"
    (is (thrown? AssertionError (yx -1 2)))
    (is (thrown? AssertionError (yx 0 3)))
    (is
      (= (yx 1 2) {:x 1 :y 2}))
    )
  )

(deftest matrix
  (testing "Matrix is converted to table"
    (let [result (to-table [[:x :e :x]
                            [:o :x :e]
                            [:x :e :o]])]
      (are [x y] (= x y)
                 (get-cell-value result (yx 0 0)) :x
                 (get-cell-value result (yx 0 1)) :e
                 (get-cell-value result (yx 0 2)) :x
                 (get-cell-value result (yx 1 0)) :o
                 (get-cell-value result (yx 1 1)) :x
                 (get-cell-value result (yx 1 2)) :e
                 (get-cell-value result (yx 2 0)) :x
                 (get-cell-value result (yx 2 1)) :e
                 (get-cell-value result (yx 2 2)) :o
                 ))))

(deftest table
  (let [table (to-table [[:x :o :x]
                         [:x :x :x]
                         [:x :e :o]])]
    (testing "elements of table can be found by indexes"
      (are [x y] (= x y)
                 (:value (get-cell table (yx 0 1))) :o
                 (:value (get-cell table (yx 1 1))) :x
                 (:value (get-cell table (yx 2 2))) :o
                 (:value (get-cell table (yx 2 1))) :e))

    (testing "elements cant be inserted in already filled cell"
      (is (thrown? AssertionError (fill-cell table :x (yx 1 1)))))

    (testing "elements can be inserted in empty cell"
      (are [x y] (= x y)
                 (get-cell-value (fill-cell table :o (yx 2 1)) (yx 2 1)) :o
                 )))
  )

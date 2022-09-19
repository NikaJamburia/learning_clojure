(ns learning-clojure.ttt.tic-tac-toe-core-test
  (:require [clojure.test :refer :all]
            [learning-clojure.ttt.tic-tac-toe-core :refer :all]))

(deftest coordinates
  (testing "tic tac toe coordinates can't be less than 0 or more then 2"
    (is (thrown-with-msg? IllegalStateException #"Coordinate values must be from 0 to 2" (yx -1 2)))
    (is (thrown-with-msg? IllegalStateException #"Coordinate values must be from 0 to 2" (yx 0 3)))
    (is
      (= (yx 1 2) {:x 2 :y 1}))
    )
  )

(deftest grid-components
  (testing "rows can be obtained from grid"
    (let [rows (get-rows [[:x :o :e]
                          [:x :x :x]
                          [:e :e :o]])]
      (are [x y] (= x y)
                 (count rows) 3
                 (get rows 0) [:x :o :e]
                 (get rows 1) [:x :x :x]
                 (get rows 2) [:e :e :o]
                 )))

  (testing "columns can be obtained from grid"
    (let [cols (get-cols [[:x :o :e]
                          [:x :x :x]
                          [:e :e :o]])]
      (are [x y] (= x y)
                 (count cols) 3
                 (get cols 0) [:x :x :e]
                 (get cols 1) [:o :x :e]
                 (get cols 2) [:e :x :o]
                 )))

  (testing "diagonals can be obtained from grid"
    (let [diagonals (get-diagonals [[:x :o :e]
                                    [:x :x :x]
                                    [:e :e :o]])]
      (are [x y] (= x y)
                 (count diagonals) 2
                 (first diagonals) [:x :x :o]
                 (second diagonals) [:e :x :e]
                 )))

  (testing "winner can be resolved given collection of cells"
    (are [x y] (= x y)
               (winner [:x :x :x]) :x
               (winner [:o :o :o]) :o
               (winner [:o :o :e]) nil
               (winner [:e :e :e]) nil
               ))
  )

(deftest grid
  (let [grid [[:x :o :e]
              [:x :x :x]
              [:e :e :o]]]
    (testing "elements of grid can be found by indexes"
      (are [x y] (= x y)
                 (find-in-grid grid (yx 0 1)) :o
                 (find-in-grid grid (yx 1 1)) :x
                 (find-in-grid grid (yx 2 2)) :o
                 (find-in-grid grid (yx 2 1)) :e))

    (testing "grid is not full when any of the elements is :e"
      (is
        (false? (is-full? grid))))

    (testing "grid is full when none of the elements are :e"
      (is
        (true? (is-full? [[:x :o :x]
                          [:x :x :x]
                          [:x :x :o]]))))

    (testing "if nobody has out a row, column or diagonal, winner is nil"
      (is
        (= (get-winner [[:x :o :x]
                        [:x :e :x]
                        [:e :x :o]]) nil)
        ))

    (testing "empty grid has no winners"
      (is
        (= (get-winner [[:e :e :e]
                        [:e :e :e]
                        [:e :e :e]]) nil)
        ))

    (testing "whatever player fills out a row, column or diagonal is a winner"
      (are [x y] (= x y)
                 (get-winner [[:x :o :x]
                              [:x :x :x]
                              [:e :x :o]]) :x

                 (get-winner [[:x :o :x]
                              [:x :o :x]
                              [:e :o :o]]) :o

                 (get-winner [[:e :x :e]
                              [:e :x :e]
                              [:e :x :e]]) :x

                 (get-winner [[:o :o :x]
                              [:x :o :x]
                              [:x :x :o]]) :o
                 ))

    (testing "elements cant be inserted in already filled cell"
      (is (thrown? AssertionError (fill-cell grid :x (yx 1 1)))))

    (testing "elements can be inserted in empty cell"
      (are [x y] (= x y)
                 (find-in-grid (fill-cell grid :o (yx 2 1)) (yx 2 1)) :o
                 (find-in-grid (fill-cell grid :x (yx 0 2)) (yx  0 2)) :x
                 ))))


(ns learning-clojure.ttt.tic-tac-toe-game-test
  (:require [clojure.test :refer :all]
            [learning-clojure.ttt.tic-tac-toe-game :refer :all]
            [learning-clojure.ttt.tic-tac-toe-core :refer :all]))

(defn- game-with-grid [player-x player-o grid]
  {:id (new-id)
   :player-x player-x
   :player-o player-o
   :next-move-by player-x
   :grid grid
   :winner nil})

(deftest make-move-test
  (testing "Returns game with correctly filled grid and next player"
    (let [result (make-move (start-new-game "Nika" "Beqa") {:player "Nika" :coordinates (yx 1 1)})]
      (is (= :x (find-in-grid (:grid result) (yx 1 1))))
      (is (= "Beqa" (:next-move-by result))))
      )

  (testing "When nobody has won yet winner is nil"
    (let [result (make-move (start-new-game "Nika" "Beqa") {:player "Nika" :coordinates (yx 1 1)})]
      (is (= nil (:winner result)))))

  (testing "when its a draw winner is :draw"
    (let [game (game-with-grid "Nika" "Beqa" [[:x :o :e]
                                              [:x :o :o]
                                              [:o :x :x]])
          result (make-move game {:player "Nika" :coordinates (yx 0 2)})]
      (is (= :draw (:winner result)))))

  (testing "returns player-x when :x has won"
    (let [game (game-with-grid "Nika" "Beqa" [[:x :o :e]
                                              [:x :o :x]
                                              [:o :x :x]])
          result (make-move game {:player "Nika" :coordinates (yx 0 2)})]
      (is (= "Nika" (:winner result)))))

  (testing "returns player-o when :o has won"
    (let [game (game-with-grid "Nika" "Beqa" [[:x :o :o]
                                              [:o :o :x]
                                              [:e :e :x]])
          result (-> game
                   (make-move {:player "Nika" :coordinates (yx 2 0)})
                   (make-move {:player "Beqa" :coordinates (yx 2 1)}))]
      (is (= "Beqa" (:winner result)))))

  (testing "Throws error when player is not a participant of the game"
    (is
      (thrown-with-msg?
        IllegalStateException
        #"Unknown player!"
        (make-move (start-new-game "Nika" "Beqa") {:player "Vigaca" :coordinates (yx 1 1)}))))

  (testing "Throws error when its not players turn to make a move"
    (is
      (thrown-with-msg?
        IllegalStateException
        #"Not your turn!"
        (make-move (start-new-game "Nika" "Beqa") {:player "Beqa" :coordinates (yx 1 1)}))))

  (testing "Throws error when winner is already determined"
    (let [draw-game (assoc (start-new-game "Nika" "Beqa") :winner :draw)]
      (is
        (thrown-with-msg?
          IllegalStateException
          #"Game already finished!"
          (make-move draw-game {:player "Nika" :coordinates (yx 1 1)})))))

  )

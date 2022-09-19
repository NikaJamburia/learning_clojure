(ns learning-clojure.ttt.tic-tac-toe-game
  (:require [learning-clojure.ttt.tic-tac-toe-core :refer :all]
            [learning-clojure.core :refer :all])
  (:import (java.util UUID)))

(defn new-id []
  (.toString (UUID/randomUUID)))

(defn start-new-game [player-x player-o]
  {:id (new-id)
   :player-x player-x
   :player-o player-o
   :next-move-by player-x
   :grid (new-grid)
   :winner nil})

(defn get-player-for [symbol game]
  {:pre [(check-state #(or (= :x symbol) (= :o symbol)) "Symbol must be either :x or :o")]}
  (if (= :x symbol)
    (:player-x game)
    (:player-o game)))

(defn flip [tic-tac]
  {:pre [(check-state #(or (= :x tic-tac) (= :o tic-tac)) "Symbol must be either :x or :o")]}
  (if (= :x tic-tac) :o :x))

(defn make-move [game make-move-params]
  {:pre [(check-state #(or (= (:player make-move-params) (:player-x game)) (= (:player make-move-params) (:player-o game))) "Unknown player!")
         (check-state #(= (:player make-move-params) (:next-move-by game)) "Not your turn!")
         (check-state #(= nil (:winner game)) "Game already finished!")]}
  (let [
        player (:player make-move-params)
        coordinates (:coordinates make-move-params)
        players-symbol (if (= player (:player-x game)) :x :o)
        opponent (get-player-for (flip players-symbol) game)
        updated-grid (fill-cell (:grid game) players-symbol coordinates)
        ]
    (-> game
        (assoc :grid updated-grid)
        (assoc :next-move-by opponent)
        (assoc :winner (let [winning-symbol (get-winner updated-grid)]
          (if (= nil winning-symbol)
            (if (is-full? updated-grid) :draw nil)
            (get-player-for winning-symbol game))))
        )))

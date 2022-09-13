(ns learning-clojure.ttt.tic-tac-toe-game
  (:require [learning-clojure.ttt.tic-tac-toe-core :refer :all])
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

(defn make-move [player game-id coordinates]
  )


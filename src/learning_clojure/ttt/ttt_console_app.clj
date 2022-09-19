(ns learning-clojure.ttt.ttt-console-app
  (:require [learning-clojure.ttt.tic-tac-toe-game :refer :all]
            [learning-clojure.ttt.tic-tac-toe-core :refer :all]
            [clojure.string :as str]))

(defn get-player [tag]
  (println (str "Enter Player " tag " name:"))
  (read-line))

(defn get-move-coordinates []
  (println "Enter next move coordinates:")
  (let [input (read-line)
        split-in (vec (str/split input #" "))]
    (yx (Integer/parseInt (first split-in)) (Integer/parseInt (second split-in)))))

(defn print-game [game]
  (println (get (:grid game) 0))
  (println (get (:grid game) 1))
  (println (get (:grid game) 2))
  (println (if (= :draw (:winner game))
    "Draw"
    (if (= nil (:winner game))
      (str "\nNext move by " (:next-move-by game) "")
      (str (:winner game) " Won!"))))

  )

(defn game-cycle [game]
  (println)
  (print-game game)
  (if (= nil (:winner game))
    (recur (try
             (make-move game {:player (:next-move-by game) :coordinates (get-move-coordinates)})
             (catch Throwable e
               (println (str "ERROR: " (.getMessage e) ". Try again"))
               game)))
    "End")

  )

(defn start-game []
  (let [player-x (get-player "X")
        player-o (get-player "O")
        game (start-new-game player-x player-o)]
    (game-cycle game))
  )

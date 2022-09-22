(ns learning-clojure.ttt.ttt-desktop
  (:require [learning-clojure.ttt.tic-tac-toe-game :refer :all]
            [learning-clojure.ttt.tic-tac-toe-core :refer :all])
  (:import (javax.swing JFrame BorderFactory JButton JPanel JLabel JTextField BoxLayout)
           (java.awt Dimension Color BorderLayout CardLayout)
           (java.awt.event ActionListener)))

(def cell-display-styles {:x {:txt "X" :txt-color (Color/RED)}
                          :o {:txt "O" :txt-color (Color/GREEN)}
                          :e {:txt "" :txt-color (Color/RED)}})

(def cell-size 50)

(def grid-layout-matrix
  (vec (for [outer [0 1 2]]
         [[(* cell-size 0) (* cell-size outer)]
          [(* cell-size 1) (* cell-size outer)]
          [(* cell-size 2) (* cell-size outer)]]
         )))

(defn display-game [game-panel frame]
  (.removeAll (.getContentPane frame))
  (.revalidate frame)
  (.repaint frame)
  (.add frame game-panel (BorderLayout/CENTER))
  (.setVisible frame true))

(defn create-cell-button [game grid-coordinates frame on-click]
  (let [value (find-in-grid (:grid game) grid-coordinates)
        style (value cell-display-styles)
        btn (new JButton (:txt style))]
    (.setBounds btn (* (:x grid-coordinates) cell-size) (* (:y grid-coordinates) cell-size) cell-size cell-size)
    (.setBorder btn (BorderFactory/createLineBorder (Color/BLACK)))
    (.setForeground btn (:txt-color style))
    (.setBackground btn (Color/WHITE))
    (.setFocusable btn false)
    (.setFont btn (.deriveFont (.getFont btn) (float 20)))
    (if (and (= value :e) (= (:winner game) nil))
      (.addActionListener btn (proxy [ActionListener] []
                                (actionPerformed [event]
                                  (on-click (make-move game {:player (:next-move-by game) :coordinates grid-coordinates}) frame))
                                  )))
    btn))

(defn grid-as-buttons [game frame on-click]
  (vec (for [outer [0 1 2]
             inner [0 1 2]]
         (let [c (yx outer inner)]
           (create-cell-button game c frame on-click)))))

(defn create-panel []
  (let [panel (new JPanel)]
    (.setVisible panel true)
    (.setLayout panel nil)
    panel))

(defn create-label [text text-color]
  (let [label (new JLabel)]
    (.setText label text)
    (.setBounds label 0 (* 3 cell-size) 1000 50)
    (.setForeground label text-color)
    (.setFont label (.deriveFont (.getFont label) (float 15)))
    label))

(defn create-game-state-panel [game frame]
  (let [panel (create-panel)
        displays (grid-as-buttons game frame (fn [g f] (display-game (create-game-state-panel g f) frame)))]
    (println game)
    (vec (map #(.add panel %) displays))
    (.add panel (if (= :draw (:winner game))
                  (create-label "Draw" (Color/BLACK))
                  (if (= nil (:winner game))
                    (create-label (str "\nNext move by " (:next-move-by game) "") (Color/BLACK))
                    (create-label (str (:winner game) " Won!") (Color/BLUE)))))
    panel))

(defn create-text-field [label]
  (let [text-field (new JTextField label)]
    (.setPreferredSize text-field (new Dimension 200 25))
    (.setMaximumSize text-field (new Dimension 200 25))
    text-field))

(defn create-start-btn [player-x-field player-o-field top-frame]
  (let [btn (new JButton "Start game")]
    (.addActionListener btn (proxy [ActionListener] []
                              (actionPerformed [event]
                                (display-game
                                  (create-game-state-panel
                                    (start-new-game (.getText player-x-field) (.getText player-o-field)) top-frame)
                                  top-frame))))
    btn))

(defn player-selection-panel [frame]
  (let [panel (new JPanel)
        player-x-field (create-text-field "Player X")
        player-o-field (create-text-field "Player O")
        start-button (create-start-btn player-x-field player-o-field frame)]
    (.setVisible panel true)
    (.setLayout panel (new BoxLayout panel (BoxLayout/PAGE_AXIS)))
    (.add panel player-x-field)
    (.add panel player-o-field)
    (.add panel start-button)
    (.setMaximumSize panel (new Dimension 750 200))
    panel))

(defn game []
  (let [frame (new JFrame)]
    (.setPreferredSize frame (new Dimension 750 500))
    (.setLayout frame (new CardLayout))
    (.setDefaultCloseOperation frame JFrame/EXIT_ON_CLOSE)
    (.pack frame)
    (.add frame (player-selection-panel frame) (BorderLayout/CENTER))
    (.setVisible frame true)))

(defn -main[& args]
  (game))




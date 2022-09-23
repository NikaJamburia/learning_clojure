(ns learning-clojure.3d.3d-visual
  (:require [learning-clojure.3d.3d-core :refer :all]
            [learning-clojure.3d.3d-meshes :refer :all])
  (:import (javax.swing JFrame JPanel Timer)
           (java.awt Dimension Color)
           (java.awt.event ActionListener)))

(def point-color (Color/RED))
(def line-color (Color/WHITE))
(def point-size 5)
(def start-millis (System/currentTimeMillis))
(def repaint-millis 30)

(defn third [collection]
  (get collection 2))

(defn fill-point [g x y]
  (.setColor g point-color)
  (.fillRect g x y point-size point-size)
  ())

(defn draw-line [points g]
  (.setColor g line-color)
  (.drawLine g (:x (first points)) (:y (first points)) (:x (second points)) (:y (second points))))

(defn paint-triangle [triangle g]
  (let [tri-points (:points triangle)]
    (draw-line [(first tri-points) (second tri-points)] g)
    (draw-line [(second tri-points) (third tri-points)] g)
    (draw-line [(first tri-points) (third tri-points)] g)
    (vec (map #(fill-point g (:x %) (:y %)) tri-points))))

(defn create-canvas [mesh]
  (proxy [JPanel] []
    (paintComponent [g]
                    (proxy-super paintComponent g)
                    (vec (map #(paint-triangle % g) (:triangles mesh))))
    (getPreferredSize [] (new Dimension (:width window-size) (:height window-size)))))

(defn mesh-to-display [theta]
  (-> mesh-cube
      (rotate-mesh theta)
      (project-to-3d)))

(defn repaint-canvas [top-panel canvas]
  (.removeAll top-panel)
  (.revalidate top-panel)
  (.repaint top-panel)
  (.setBackground canvas (Color/BLACK))
  (.add top-panel canvas)
  (.setVisible top-panel true))

(defn repaint-action-listener [top-panel]
  (proxy [ActionListener] []
    (actionPerformed [e]
      (let [time-elapsed (/ (- (.getWhen e) start-millis) 1500)]
        (repaint-canvas top-panel (create-canvas (mesh-to-display time-elapsed)))))))

(defn -main[& args]
  (let [frame (new JFrame)
        top-panel (new JPanel)
        canvas (create-canvas (mesh-to-display 1))
        timer (new Timer repaint-millis nil)]
    (.setPreferredSize frame (new Dimension (:width window-size) (:height window-size)))
    (.setDefaultCloseOperation frame JFrame/EXIT_ON_CLOSE)
    (.setBackground canvas (Color/BLACK))
    (.setPreferredSize top-panel (new Dimension (:width window-size) (:height window-size)))
    (.add top-panel canvas)
    (.add frame top-panel)
    (.pack frame)
    (.setVisible frame true)
    (.addActionListener timer (repaint-action-listener top-panel))
    (.start timer)
    ))
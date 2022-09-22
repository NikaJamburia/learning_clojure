(ns learning-clojure.3d.3d-visual
  (:require [learning-clojure.3d.3d-core :refer :all]
            [learning-clojure.3d.3d-meshes :refer :all])
  (:import (javax.swing JFrame JPanel)
           (java.awt Dimension Color)))

(def point-color (Color/RED))
(def line-color (Color/WHITE))
(def point-size 5)

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
    (vec (map #(fill-point g (:x %) (:y %)) tri-points))
    ))

(defn canvas [mesh]
  (proxy [JPanel] []
    (paintComponent [g]
                    (proxy-super paintComponent g)
                    (vec (map #(paint-triangle % g) (:triangles mesh))))
    (getPreferredSize [] (new Dimension (:width window-size) (:height window-size)))))

(defn mesh-to-display []
  (project-to-3d mesh-cube))

(defn start []
  (let [frame (new JFrame)
        panel (canvas (mesh-to-display))]
    (.setPreferredSize frame (new Dimension (:width window-size) (:height window-size)))
    (.setDefaultCloseOperation frame JFrame/EXIT_ON_CLOSE)
    (.setBackground panel (Color/BLACK))
    (.add frame panel)
    (.pack frame)
    (.setVisible frame true)
    )
)


(defn -main[& args]
  (start))
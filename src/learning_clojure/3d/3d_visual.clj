(ns learning-clojure.3d.3d-visual
  (:require [learning-clojure.3d.3d-core :refer :all]
            [learning-clojure.3d.3d-meshes :refer :all]
            [learning-clojure.3d.3d-import :refer :all])
  (:import (javax.swing JFrame JPanel Timer)
           (java.awt Dimension Color Polygon)
           (java.awt.event ActionListener)))

(def point-color (Color/RED))
(def line-color (Color/WHITE))
(def point-size 5)
(def start-millis (System/currentTimeMillis))
(def repaint-millis 30)

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

(defn adjust-color-part [part lighting]
  (let [abs-value (Math/abs (float lighting))
        multiplied (Math/ceil (* abs-value part))]
    (if (> multiplied 255) 255 (int multiplied))))

(defn adjust-color [color lighting]
  (if (neg? lighting)
    (let [value (int (Math/ceil (* (Math/abs (float lighting)) 255)))]
      (new Color
           (adjust-color-part (.getRed color) lighting)
           (adjust-color-part (.getGreen color) lighting)
           (adjust-color-part (.getBlue color) lighting)))
    (Color/BLACK)))


(defn fill-triangle [triangle g]
  (let [xs (int-array (map #(:x %) (:points triangle)))
        ys (int-array (map #(:y %) (:points triangle)))
        poly (new Polygon xs ys 3)
        color (adjust-color (new Color 62 126 88) (:lighting triangle))]
    (.setColor g color)
    (.fillPolygon g poly)))

(defn create-canvas [mesh]
  (proxy [JPanel] []
    (paintComponent [g]
                    (proxy-super paintComponent g)
                    (vec (map #(fill-triangle % g) (:triangles mesh))))
    (getPreferredSize [] (new Dimension (:width window-size) (:height window-size)))))

(defn mesh-to-display [mesh rotation-theta]
  (-> mesh
      (rotate-mesh rotation-theta)
      (project-to-3d)))

(defn repaint-canvas [top-panel canvas]
  (.removeAll top-panel)
  (.revalidate top-panel)
  (.repaint top-panel)
  (.setBackground canvas (Color/BLACK))
  (.add top-panel canvas)
  (.setVisible top-panel true))

(defn repaint-action-listener [top-panel mesh]
  (proxy [ActionListener] []
    (actionPerformed [e]
      (let [time-elapsed (/ (- (.getWhen e) start-millis) 1500)]
        (repaint-canvas top-panel (create-canvas (mesh-to-display mesh time-elapsed)))))))

(defn -main[& args]
  (let [frame (new JFrame)
        top-panel (new JPanel)
        ;mesh (import-mesh-from "teapot.obj")
        mesh mesh-cube
        canvas (create-canvas (mesh-to-display mesh 0))
        timer (new Timer repaint-millis nil)]
    (.setPreferredSize frame (new Dimension (:width window-size) (:height window-size)))
    (.setDefaultCloseOperation frame JFrame/EXIT_ON_CLOSE)
    (.setBackground canvas (Color/BLACK))
    (.setPreferredSize top-panel (new Dimension (:width window-size) (:height window-size)))
    (.add top-panel canvas)
    (.add frame top-panel)
    (.pack frame)
    (.setVisible frame true)
    (.addActionListener timer (repaint-action-listener top-panel mesh))
    (.start timer)
    ))
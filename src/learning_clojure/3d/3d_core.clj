(ns learning-clojure.3d.3d-core
  (:require [learning-clojure.core :refer :all]))

; CONFIG
(def window-size {:width 1024 :height 768})
(def near 0.1)
(def far 1000)
(def aspect (/ (:height window-size) (:width window-size)))
(def fov 90)
(def fov-rad (/ 1 (Math/tan (* fov (* (float 3.14159) (/ 0.5 180))))))

(def projection-matrix (let [m-0-0 (* aspect fov-rad)
                             m-1-1 fov-rad
                             m-2-2 (/ far (- far near))
                             m-3-2 (/ (* (unchecked-negate far) near) (- far near))]
                         [[m-0-0 0 0 0]
                          [0 m-1-1 0 0]
                          [0 0 m-2-2 1]
                          [0 0 m-3-2 0]]))

; MODEL
(defn point-3d [x y z]
  {:pre [(check-state #(number? x) "All coordinates must be numbers")
         (check-state #(number? y) "All coordinates must be numbers")
         (check-state #(number? z) "All coordinates must be numbers")]}
  {:x x :y y :z z})

(defn triangle [points]
  {:pre [(check-state #(= 3 (count points)) "Triangle must have 3 points!")]}
  {:points points})

(defn create-mesh [triangles]
  {:triangles triangles})

(defn m [matrix i1 i2]
  (get (get matrix i1) i2))


; CALCULATIONS
(defn multiply-3d-point-by-matrix [pt matrix]
  (let [w (+
            (* (:x pt) (m matrix 0 3))
            (* (:y pt) (m matrix 1 3))
            (* (:z pt) (m matrix 2 3))
            (m matrix 3 3))
        multiplied {:x (+
                         (* (:x pt) (m matrix 0 0))
                         (* (:y pt) (m matrix 1 0))
                         (* (:z pt) (m matrix 2 0))
                         (m matrix 3 0))
                    :y (+
                         (* (:x pt) (m matrix 0 1))
                         (* (:y pt) (m matrix 1 1))
                         (* (:z pt) (m matrix 2 1))
                         (m matrix 3 1))
                    :z (+
                         (* (:x pt) (m matrix 0 2))
                         (* (:y pt) (m matrix 1 2))
                         (* (:z pt) (m matrix 2 2))
                         (m matrix 3 2))}]
    (if (not= 0 w)
      {:x (/ (:x multiplied) w)
       :y (/ (:y multiplied) w)
       :z (/ (:z multiplied) w)}
      multiplied)))

(defn translate-point [pt amount]
  (let [new-z (+ amount (:z pt))]
    {:x (:x pt)
     :y (:y pt)
     :z (float new-z)}))

(defn scale-point [pt]
  (point-3d
    (* (inc (:x pt)) (* 0.5 (:width window-size)))
    (* (inc (:y pt)) (* 0.5 (:height window-size)))
    (:z pt)))

(defn project-triangle [tri]
  (let [pts (:points tri)
        points-2d (->> pts
                       (map #(translate-point % (float 3)))
                       (map #(multiply-3d-point-by-matrix % projection-matrix))
                       (map #(scale-point %)))]
    (triangle (vec points-2d))))

(defn project-to-3d [mesh]
  (create-mesh (vec (map #(project-triangle %) (:triangles mesh)))))
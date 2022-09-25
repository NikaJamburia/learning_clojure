(ns learning-clojure.3d.3d-core
  (:require [learning-clojure.core :refer :all]))

; CONFIG
(def window-size {:width 1024 :height 768})
(def near 0.1)
(def far 1000)
(def aspect (/ (:height window-size) (:width window-size)))
(def fov 90)
(def view-space-scale (/ far (- far near)))
(def fov-rad (/ 1 (Math/tan (* fov (* (float 3.14159) (/ 0.5 180))))))

(def projection-matrix
  (let [m-0-0 (* aspect fov-rad)
        m-1-1 fov-rad
        m-2-2 view-space-scale
        m-3-2 (unchecked-negate (* view-space-scale near))]
    [[m-0-0 0 0 0]
     [0 m-1-1 0 0]
     [0 0 m-2-2 1]
     [0 0 m-3-2 0]]))

(defn rotation-matrix-x [theta]
  (let [m-1-1 (Math/cos (* 0.5 theta))
        m-1-2 (Math/sin (* 0.5 theta))
        m-2-1 (unchecked-negate (Math/sin (* 0.5 theta)))
        m-2-2 (Math/cos (* 0.5 theta))]
    [[1 0     0     0]
     [0 m-1-1 m-1-2 0]
     [0 m-2-1 m-2-2 0]
     [0 0     0     1]]))

(defn rotation-matrix-z [theta]
  (let [m-0-0 (Math/cos theta)
        m-0-1 (Math/sin theta)
        m-1-0 (unchecked-negate (Math/sin theta))
        m-1-1 (Math/cos theta)]
    [[m-0-0 m-0-1 0 0]
     [m-1-0 m-1-1 0 0]
     [0     0     1 0]
     [0     0     0 1]]))

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

(defn third [collection]
  (get collection 2))


; CALCULATIONS
(def camera (point-3d 0 0 0))

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
  (assoc pt :z (+ amount (:z pt))))

(defn scale-point [pt]
  (point-3d
    (* (inc (:x pt)) (* 0.5 (:width window-size)))
    (* (inc (:y pt)) (* 0.5 (:height window-size)))
    (:z pt)))

(defn calc-normal [point1 point2]
  (point-3d
    (- (:x point1) (:x point2))
    (- (:y point1) (:y point2))
    (- (:z point1) (:z point2))))

(defn calc-cross-product [normal1 normal2]
  (point-3d
    (- (* (:y normal1) (:z normal2)) (* (:z normal1) (:y normal2)))
    (- (* (:z normal1) (:x normal2)) (* (:x normal1) (:z normal2)))
    (- (* (:x normal1) (:y normal2)) (* (:y normal1) (:x normal2)))))

(defn square [n]
  (* n n))

(defn normalize [point]
  (let [length (Math/sqrt (+ (square (:x point)) (square (:y point)) (square (:z point)) ))]
    (point-3d
      (/ (:x point) length)
      (/ (:y point) length)
      (/ (:z point) length))))

(defn subtract-camera [pt]
  (point-3d (- (:x pt) (:x camera)) (- (:y pt) (:y camera)) (- (:z pt) (:z camera))))

(defn calculate-dot-product [p1 p2]
  (let [camera-subtracted (subtract-camera p2)]
    (+
      (* (:x p1) (:x camera-subtracted))
      (* (:y p1) (:y camera-subtracted))
      (* (:z p1) (:z camera-subtracted))
      )))

(defn calculate-triangle-normal [tri]
  (let [pts (:points tri)
        normal1 (calc-normal (second pts) (first pts))
        normal2 (calc-normal (third pts) (first pts))]
    (normalize (calc-cross-product normal1 normal2))))

(defn is-visible? [triangle]
  (let [pts (:points triangle)
        normal (calculate-triangle-normal triangle)
        dot-pr (calculate-dot-product normal (first pts))]
    (neg? dot-pr)))

(defn get-lighting [tri]
  (let [light-direction (normalize (point-3d 0 0 01))
        triangle-normal (calculate-triangle-normal tri)
        dot-pr (calculate-dot-product triangle-normal light-direction)]
    dot-pr))

(defn translate-triangle [tri]
  (triangle (vec (map #(translate-point % (float 3)) (:points tri)))))

(defn project-triangle [tri]
  (let [translated (translate-triangle tri)
        lighting-value (get-lighting translated)
        points-2d (->> (:points translated)
                       (map #(multiply-3d-point-by-matrix % projection-matrix))
                       (map #(scale-point %)))]
    (if (is-visible? translated)
      (assoc tri :points (vec points-2d) :lighting lighting-value)
      (assoc tri :lighting lighting-value))))

(defn rotate-triangle [tri theta]
  (let [points-2d (->> (:points tri)
                       (map #(multiply-3d-point-by-matrix % (rotation-matrix-z theta)))
                       (map #(multiply-3d-point-by-matrix % (rotation-matrix-x theta)))
                       )]
    (assoc tri :points (vec points-2d))))

(defn rotate-mesh [mesh theta]
  (create-mesh (vec (map #(rotate-triangle % theta) (:triangles mesh)))))

(defn project-to-3d [mesh]
  (create-mesh (vec (map #(project-triangle %) (:triangles mesh)))))
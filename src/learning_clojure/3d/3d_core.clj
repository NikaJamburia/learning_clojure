(ns learning-clojure.3d.3d-core
  (:require [learning-clojure.core :refer :all]
            [learning-clojure.3d.3d-util :refer :all]))

; CONFIG
(def window-size {:width 1024 :height 768})
(def near 0.1)
(def far 1000)
(def aspect (/ (:height window-size) (:width window-size)))
(def fov 90)
(def translate-z-by (float 8))
(def view-space-scale (/ far (- far near)))
(def fov-rad (/ 1 (Math/tan (* fov (* (float 3.14159) (/ 0.5 180))))))

; MODEL
(defn vector-3d [x y z]
  {:pre [(check-state #(number? x) (str "All coordinates must be numbers" z))
         (check-state #(number? y) (str "All coordinates must be numbers" z))
         (check-state #(number? z) (str "All coordinates must be numbers" z))]}
  {:x x :y y :z z})

(defn create-triangle [vectors]
  {:pre [(check-state #(= 3 (count vectors)) "Triangle must have 3 vectors!")]}
  {:vectors vectors})

(defn create-mesh [triangles]
  {:triangles triangles})

; MATRIX
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


; CALCULATIONS
(def camera (vector-3d 0 0 0))

(defn multiply-3d-vector-by-matrix [vector matrix]
  (let [w (+
            (* (:x vector) (m matrix 0 3))
            (* (:y vector) (m matrix 1 3))
            (* (:z vector) (m matrix 2 3))
            (m matrix 3 3))
        multiplied {:x (+
                         (* (:x vector) (m matrix 0 0))
                         (* (:y vector) (m matrix 1 0))
                         (* (:z vector) (m matrix 2 0))
                         (m matrix 3 0))
                    :y (+
                         (* (:x vector) (m matrix 0 1))
                         (* (:y vector) (m matrix 1 1))
                         (* (:z vector) (m matrix 2 1))
                         (m matrix 3 1))
                    :z (+
                         (* (:x vector) (m matrix 0 2))
                         (* (:y vector) (m matrix 1 2))
                         (* (:z vector) (m matrix 2 2))
                         (m matrix 3 2))}]
    (if (not= 0 w)
      {:x (/ (:x multiplied) w)
       :y (/ (:y multiplied) w)
       :z (/ (:z multiplied) w)}
      multiplied)))

(defn translate-z [vec-3d amount]
  (assoc vec-3d :z (+ amount (:z vec-3d))))

(defn scale-vector [vector]
  (assoc vector
    :x (* (inc (:x vector)) (* 0.5 (:width window-size)))
    :y (* (inc (:y vector)) (* 0.5 (:height window-size)))))

(defn vector-subtract [vec1 vec2]
  (vector-3d
    (- (:x vec1) (:x vec2))
    (- (:y vec1) (:y vec2))
    (- (:z vec1) (:z vec2))))

(defn calc-cross-product [vec1 vec2]
  (vector-3d
    (- (* (:y vec1) (:z vec2)) (* (:z vec1) (:y vec2)))
    (- (* (:z vec1) (:x vec2)) (* (:x vec1) (:z vec2)))
    (- (* (:x vec1) (:y vec2)) (* (:y vec1) (:x vec2)))))

(defn normalize [vector]
  (let [length (Math/sqrt (+ (square (:x vector)) (square (:y vector)) (square (:z vector)) ))]
    (assoc vector
      :x (/ (:x vector) length)
      :y (/ (:y vector) length)
      :z (/ (:z vector) length))))

(defn calculate-dot-product [vec1 vec2]
  (+
    (* (:x vec1) (:x vec2))
    (* (:y vec1) (:y vec2))
    (* (:z vec1) (:z vec2))))

(defn calculate-triangle-normal [tri]
  (let [vectors (:vectors tri)
        line1 (vector-subtract (second vectors) (first vectors))
        line2 (vector-subtract (third vectors) (first vectors))]
    (normalize (calc-cross-product line1 line2))))

(defn is-visible? [triangle]
  (let [vectors (:vectors triangle)
        normal (calculate-triangle-normal triangle)
        dot-pr (calculate-dot-product normal (vector-subtract (first vectors) camera))]
    (neg? dot-pr)))

(defn get-lighting [tri]
  (let [light-direction (normalize (vector-3d 0 0 1))
        triangle-normal (calculate-triangle-normal tri)
        dot-pr (calculate-dot-product triangle-normal light-direction)]
    dot-pr))

(defn translate-triangle [tri]
  (create-triangle (vec (map #(translate-z % translate-z-by) (:vectors tri)))))

(defn get-midpoint [tri]
  (let [vectors (:vectors tri)
        coords-sum (+
                     (:z (first vectors))
                     (:z (second vectors))
                     (:z (third vectors)))]
    (/ coords-sum 3)))

(defn compare-triangles-by-z [tri1 tri2]
  (> (get-midpoint tri1) (get-midpoint tri2)))

(defn project-triangle [tri]
  (let [translated (translate-triangle tri)
        lighting-value (get-lighting translated)
        vecs-2d (->> (:vectors translated)
                       (map #(multiply-3d-vector-by-matrix % projection-matrix))
                       (map #(scale-vector %)))]
    (if (is-visible? translated)
      (assoc tri :vectors (vec vecs-2d) :lighting lighting-value)
      (assoc tri :lighting lighting-value))))

(defn rotate-triangle [tri theta]
  (let [vectors-2d (->> (:vectors tri)
                       (map #(multiply-3d-vector-by-matrix % (rotation-matrix-z theta)))
                       (map #(multiply-3d-vector-by-matrix % (rotation-matrix-x theta)))
                       (vec))]
    (assoc tri :vectors vectors-2d)))

(defn rotate-mesh [mesh theta]
  (let [rotated (->> (:triangles mesh)
                     (map #(rotate-triangle % theta))
                     (vec))]
    (assoc mesh :triangles rotated)))

(defn project-to-3d [mesh]
  (let [tris (:triangles mesh)
        processed (->> tris
                       (map #(project-triangle %))
                       (sort #(compare-triangles-by-z %1 %2))
                       (vec))]
    (assoc mesh :triangles processed)))
(ns learning-clojure.ttt.tic-tac-toe-core)

(defn first-by [prec col]
  (first (filter prec col)))

(defn yx [y x]
  {:pre [(and (>= x 0) (<= x 2))
         (and (>= y 0) (<= y 2))]}
  { :x x :y y})

(defn find-in-grid [grid coordinates]
  (get (get grid (:y coordinates)) (:x coordinates)))

(defn fill-cell [grid value coordinates]
  {:pre [(= :e (find-in-grid grid coordinates))]}
  (vec (map (fn [outer-index]
              (let [inner-indexes [0 1 2]]
                (vec (map (fn [inner-index]
                            (if (= (yx outer-index inner-index) coordinates)
                              value
                              (get (get grid outer-index) inner-index)))
                          inner-indexes))))
            [0 1 2])))

(defn all-values [grid]
  (for [outer [0 1 2]
        inner [0 1 2]]
    (find-in-grid grid (yx outer inner))))

(defn is-full? [grid]
  (every? #(not= % :e) (all-values grid)))

(defn get-rows[grid]
  [(get grid 0) (get grid 1) (get grid 2)])

(defn get-cols [grid]
  (vec (for [col-index [0 1 2]]
         [(find-in-grid grid (yx 0 col-index)) (find-in-grid grid (yx 1 col-index)) (find-in-grid grid (yx 2 col-index))])))

(defn get-diagonals [grid]
  [[(find-in-grid grid (yx 0 0)) (find-in-grid grid (yx 1 1)) (find-in-grid grid (yx 2 2))]
   [(find-in-grid grid (yx 0 2)) (find-in-grid grid (yx 1 1)) (find-in-grid grid (yx 2 0))]])

(defn winner [cells]
  (if (and (not= (get cells 0) :e) (and (= (get cells 0) (get cells 1)) (= (get cells 0) (get cells 2))))
    (get cells 0)
    nil))

(defn get-winner [grid]
  (let [rows (get-rows grid)
        cols (get-cols grid)
        diagonals (get-diagonals grid)
        rows-winner (first-by #(not= nil %) (map winner rows))
        cols-winner (first-by #(not= nil %) (map winner cols))
        diagonal-winner (first-by #(not= nil %) (map winner diagonals))]
    (first-by #(not= nil %) [rows-winner cols-winner diagonal-winner])))

(defn new-grid []
  [[:e :e :e]
   [:e :e :e]
   [:e :e :e]])
(ns learning-clojure.ttt.tic-tac-toe-core)

(defn yx [x y]
  {:pre [(and (>= x 0) (<= x 2))
         (and (>= y 0) (<= y 2))]}
  { :x x :y y})

(defn cell [value coordinates]
  { :value value :coordinates coordinates })

(defn is-at? [cell coordinates]
  (= (:coordinates cell) coordinates))

(defn find-in-matrix [matrix i1 i2]
  (get (get matrix i1) i2))

(defn to-table [matrix]
  [
   (cell (find-in-matrix matrix 0 0) (yx 0 0)) (cell (find-in-matrix matrix 0 1) (yx 0 1)) (cell (find-in-matrix matrix 0 2) (yx 0 2))
   (cell (find-in-matrix matrix 1 0) (yx 1 0)) (cell (find-in-matrix matrix 1 1) (yx 1 1)) (cell (find-in-matrix matrix 1 2) (yx 1 2))
   (cell (find-in-matrix matrix 2 0) (yx 2 0)) (cell (find-in-matrix matrix 2 1) (yx 2 1)) (cell (find-in-matrix matrix 2 2) (yx 2 2))
   ])



(defn get-cell [table coordinates]
  (first (filter #(is-at? % coordinates) table)))

(defn get-cell-value [table coordinates]
  (:value (first (filter #(is-at? % coordinates) table))))

(defn fill-cell [table value coordinates]
  {:pre [(= :e (:value (get-cell table coordinates)))]}
  (map
    (fn [old-cell] (if (is-at? old-cell coordinates)
       (cell value coordinates)
       old-cell)) table))
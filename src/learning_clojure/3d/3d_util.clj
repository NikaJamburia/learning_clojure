(ns learning-clojure.3d.3d-util)

(defn m [matrix i1 i2]
  (get (get matrix i1) i2))

(defn third [collection]
  (get collection 2))

(defn square [n]
  (* n n))
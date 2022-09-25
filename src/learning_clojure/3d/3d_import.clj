(ns learning-clojure.3d.3d-import
  (:require [learning-clojure.3d.3d-core :refer :all]
            [clojure.java.io :as io]
            [clojure.string :as str]))

(defn parse-obj-line [line]
  (let [split (str/split line #" ")
        just-nums (subvec split 1)]
    (vec (map #(Float/parseFloat %) just-nums))))

(defn collect-vertices [lines]
  (->> lines
          (filter #(str/starts-with? % "v"))
          (map #(parse-obj-line %))
          (map #(point-3d
                  (first %)
                  (second %)
                  (third % )))))

(defn get-points [indexes points]
  (vec (map #(get points (dec (int %))) indexes)))

(defn collect-triangles [lines points]
  (->> lines
       (filter #(str/starts-with? % "f"))
       (map #(parse-obj-line %))
       (map #(get-points % points))
       (map #(triangle %))))

(defn import-mesh-from [filename]
  (with-open [file (io/reader (io/input-stream (io/resource filename)))]
    (let [lines (line-seq file)
          points (collect-vertices lines)
          tris (collect-triangles lines (vec points))]
      (create-mesh (vec tris)))))

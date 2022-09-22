(ns learning-clojure.3d.3d-meshes
  (:require [learning-clojure.3d.3d-core :refer :all]))

(def mesh-cube
  (create-mesh [
                ;SOUTH
                (triangle [(point-3d 0 0 0) (point-3d 0 1 0) (point-3d 1 1 0)])
                (triangle [(point-3d 0 0 0) (point-3d 1 1 0) (point-3d 1 0 0)])

                ;EAST
                (triangle [(point-3d 1 0 0) (point-3d 1 1 0) (point-3d 1 1 1)])
                (triangle [(point-3d 1 0 0) (point-3d 1 1 1) (point-3d 1 0 1)])

                ;NORTH
                (triangle [(point-3d 1 0 1) (point-3d 1 1 1) (point-3d 0 1 1)])
                (triangle [(point-3d 1 0 1) (point-3d 0 1 1) (point-3d 0 0 1)])

                ;WEST
                (triangle [(point-3d 0 0 1) (point-3d 0 1 1) (point-3d 0 1 0)])
                (triangle [(point-3d 0 0 1) (point-3d 0 1 0) (point-3d 0 0 0)])

                ;TOP
                (triangle [(point-3d 0 1 0) (point-3d 0 1 1) (point-3d 1 1 1)])
                (triangle [(point-3d 0 1 0) (point-3d 1 1 1) (point-3d 1 1 0)])

                ;BOTTOM
                (triangle [(point-3d 1 0 1) (point-3d 0 0 1) (point-3d 0 0 0)])
                (triangle [(point-3d 1 0 1) (point-3d 0 0 0) (point-3d 1 0 0)])
                ]))

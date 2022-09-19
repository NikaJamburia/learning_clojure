(ns learning-clojure.main
  (:gen-class)
  (:require [learning-clojure.core :refer :all])
  (:require [learning-clojure.birthdays :refer :all])
  (:require [learning-clojure.users :refer :all])
  (:require [learning-clojure.ttt.ttt-console-app :refer :all])
  )

;(def users-source mock-users)
(def users-source csv-users)

;(defn -main[& args]
;  (println
;    (collect-birthday-messages
;      (date "2022-11-16") users-source)))

(defn -main[& args]
  (start-game))
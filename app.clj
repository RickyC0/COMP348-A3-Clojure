(ns app 
  (:require [menu]) 
  (:require [db])
  )

(defn -main
  []
  (let [filename "grades.txt"
        students (db/store-students filename)] ; Load the students from the file

    (println "Students file loaded successfully.")
    (flush)

    (menu/menu students)))

(-main)
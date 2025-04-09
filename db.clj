(ns db
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))


(defn mapify-grades
  "Recursively processes a vector of grade data into a grade map.
   Expects that the vector length is a multiple of 3."
  ([grades] (mapify-grades grades {}))
  ([grades gmap]
   (if (< (count grades) 3)
     gmap
     (let [res (assoc gmap (first grades)
                      {:weight (nth grades 1)
                       :grade  (nth grades 2)})]
       (recur (drop 3 grades) res)))))

(defn parse-student-record
  "Parses a vector of CSV fields for a student record into a map.
   Assumes that the first three fields are id, first name, and last name, and the rest of the fields are grade data."
  [record]
  (let [id         (first record)
        first-name (second record)
        last-name  (nth record 2)
        ;; The remaining fields are grade data:
        grades     (drop 3 record)
        grade-map  (mapify-grades grades)]
    {:id id
     :first-name first-name
     :last-name last-name
     :grades grade-map}
    )
  )


(defn loadDataFile
   "Reads the given filename as a CSV file. Returns a sequence of records,
    where each record is a vector of strings."
   [filename]
   (with-open [rdr (io/reader filename)]
     (doall
      (map #(str/split % #",") (line-seq rdr))
      )
     )
  )

 ;; Processes the entire file. Returns a collection of student maps.
(defn process-file
  "Loads and parses student records from the given filename.
   Returns a sequence of student maps."
  [filename]
  (let [data (loadDataFile filename)]
    (map parse-student-record data)))

(def students (atom []))

(defn store-students
  "Processes the file and stores the student records in the 'students' atom."
  [filename]
  (reset! students (vec (process-file filename)))
 )

(defn get-students
  "Returns the current list of students."
  []
  @students)

(ns menu
  (:require [clojure.string :as str]))


  
; Display the menu and ask the user for the option
(defn showMenu
  []
  (println "\n\n*** Grade Processing Menu ***")
  (println     "-----------------------------\n")
  (println "1. List Names")
  (println "2. Display Student Record by Id")
  (println "3. Display Student Record by Lastname")
  (println "4. Display Component")
  (println "5. Display Total")
  (println "6. Exit")
  (do 
    (print "\nEnter an option: ") 
    (flush) 
    (read-line)))


(defn option1
  [students] ; 'students' here is a plain collection (vector) of student maps
  (println "****************************************")
  (println "*           STUDENT NAMES              *")
  (println "****************************************")
  ;; Print a header row with column names.
  (println (format "%-10s | %-15s | %-15s" "ID" "First Name" "Last Name"))
  (println "----------------------------------------------")
  ;; For each student, print a formatted row.
  (doseq [student students]
    (println (format "%-10s | %-15s | %-15s"
                     (:id student)
                     (:first-name student)
                     (:last-name student))))
  (println "----------------------------------------------")
  (println (format "Total Students: %d" (count students)))
  (println "****************************************\n"))

    
(defn key-students-by-id [students]
  (into {} (map (fn [student]
                  [(:id student) student])
                students)))
    
;; Function that displays the student record based on the ID
;; The ID is expected to be 8 digits long
;; The function will keep asking for the ID until a valid one is provided
(defn option2
  [students]
  
  (print "\nPlease enter the student's ID (8 digits) => ")
  (flush)
  (let [id (read-line)
        students (key-students-by-id students)] ; Convert the vector to a map for faster lookups
    (if (re-matches #"\d{8}" id) ; check if the ID is 8 digits
      (if-let [student (get students id)]
        (do
          (println "\n****************************************")
          (println "*         STUDENT INFORMATION          *")
          (println "****************************************")
          (println (format "%-10s | %-15s | %-15s" "ID" "First Name" "Last Name"))
          (println "----------------------------------------------")
          (println (format "%-10s | %-15s | %-15s"
                           (:id student)
                           (:first-name student)
                           (:last-name student)))
          (println "----------------------------------------------")
          (when-let [grades (:grades student)]
            (println "\nGrades:")
            (println (format "%-12s | %-10s | %-8s" "Component" "Weight" "Grade"))
            (println "--------------------------------------------")
            (doseq [[comp details] grades]
              (println (format "%-12s | %-10s | %-8s"
                               comp
                               (:weight details)
                               (:grade details)))))
          (println "****************************************\n"))
            
        (do
          (println "Student ID not found. Please try again.")))
            
          

      (do
        (println "Invalid input. Please enter a valid 8-digit student ID.")))))
            

(defn option3
  [students]
  (print "\nPlease enter the student's last name => ")
  (flush)
  (let [input-last-name (read-line)
        last-name (str/lower-case input-last-name)]
    
    (if (re-matches #"[a-zA-Z]+" last-name) ; check if the last name is valid
      (let [matching-students (filter #(= last-name (str/lower-case (:last-name %))) ;Check for case-insensitive match
                                      students)]
        (if (empty? matching-students)
          (println "\nNo student with that last name found. Please try again.")
          (do
            (println "\n****************************************")
            (println "*  STUDENT INFORMATION - LAST NAME   *")
            (println "****************************************")
            (println (format "%-10s | %-15s | %-15s" "ID" "First Name" "Last Name"))
            (println "----------------------------------------------")
            (doseq [student matching-students]
              (println (format "%-10s | %-15s | %-15s"
                               (:id student)
                               (:first-name student)
                               (:last-name student))))
            (println "----------------------------------------------")
            ;; For each matching student, print grade details.
            (doseq [student matching-students]
              (when-let [grades (:grades student)]
                (println (format "\nGrades for student %s:" (:id student)))
                (println (format "%-12s | %-10s | %-8s" "Component" "Weight" "Grade"))
                (println "--------------------------------------------")
                (doseq [[comp details] grades]
                  (println (format "%-12s | %-10s | %-8s"
                                   comp
                                   (:weight details)
                                   (:grade details))))
                (println)))
            (println "****************************************\n"))))
      (println "\nInvalid input. Please enter a valid last name."))))



(defn option4
  [students]
  (print "\nPlease enter the component => ")
  (flush)
  (let [component (-> (read-line)
                      str/trim
                      str/upper-case)]
    
    (if (re-matches #"[a-zA-Z1-9]+" component)  ; Validate the input is alphabetic
      (let [matching-students (filter #(contains? (:grades %) component)
                                      students)]
        (if (empty? matching-students)
          (println "\nThe component" component "was not found for any student.")
          (do
            (println "\n****************************************")
            (println "*       Grades for Component:      *")
            (println "****************************************")
            (println (format "%-10s | %-15s" "Student ID" "Grade"))
            (println "--------------------------------")
            (doseq [student matching-students]
              (println (format "%-10s | %-15s"
                               (:id student)
                               (get-in student [:grades component :grade]))))
            (println "--------------------------------")
            (println (format "Total Students with component %s: %d" component (count matching-students)))
            ;Average logic
             (let [grade-values (map #(Double/parseDouble (get-in % [:grades component :grade]))
                                    matching-students)
                  total (reduce + grade-values)
                  avg (if (pos? (count grade-values))
                        (/ total (count grade-values))
                        0)]
              (println (format "The average is: %.2f" avg)))
            (println "****************************************\n"))))
      (println "\nInvalid input. Please enter a valid component."))))


; If the menu selection is valid, call the relevant function to 
; process the selection
(defn processOption
  [option,students] 
  (if( = option "1")
     (option1 students)
    
     (if( = option "2")
        (option2 students)
       
        (if( = option "3")
           (option3 students)  
          
           (if( = option "4")
              (option4 students)   
              (println "Invalid Option, please try again"))))))


; Display the menu and get a menu item selection. Process the
; selection and then loop again to get the next menu selection
(defn menu
  [students] ; parm(s) can be provided here, if needed
  (let [option (str/trim (showMenu))]
    (if (= option "6")
      (do 
        (println "\nGood Bye\n") 
        (System/exit 0)) ; Exit the program
      (do 
        (processOption option students)
        (recur students)))))   

; ------------------------------
; Run the program. You might want to prepare the data required for the mapping operations
; before you display the menu. You don't have to do this but it might make some things easier

; A grades.txt file will be provided in the *current directory*, during grading
; In general you need to read, process, and display the file content (grades.txt) 
; You may use Java's File class to check for existence. 


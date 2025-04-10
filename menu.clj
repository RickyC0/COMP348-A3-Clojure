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

(defn option5
  [students]
  ;; Print header for the table
  (println "\n****************************************")
  (println "*         STUDENT TOTALS             *")
  (println "****************************************")
  (println (format "%-10s | %-15s | %-8s | %-8s | %-8s | %-8s | %-10s"
                   "ID" "First Name" "A1" "A2" "MIDTERM" "FINAL" "TOTAL"))
  (println "----------------------------------------------------------------------------")

  ;; Define atoms to accumulate raw sums and counts for each component,
  ;; and to accumulate the overall weighted total.
  (let [a1-sum   (atom 0)
        a2-sum   (atom 0)
        mid-sum  (atom 0)
        fin-sum  (atom 0)
        total-sum (atom 0)
        nb-a1    (atom 0)
        nb-a2    (atom 0)
        nb-mid   (atom 0)
        nb-fin   (atom 0)]

    ;; Process each student.
    (doseq [student students]
      (let [;; Retrieve grade and weight for each component.
            ;; Note: We assume grades are stored under the key :grades.
            a1   (get-in student [:grades "A1" :grade])
            a1w  (get-in student [:grades "A1" :weight])
            a2   (get-in student [:grades "A2" :grade])
            a2w  (get-in student [:grades "A2" :weight])
            mid  (get-in student [:grades "MIDTERM" :grade])
            midw (get-in student [:grades "MIDTERM" :weight])
            fin  (get-in student [:grades "FINAL" :grade])
            finw (get-in student [:grades "FINAL" :weight])

            ;; Convert grade strings to numbers (default 0 if missing)
            a1-num  (if a1 (Double/parseDouble a1) 0)
            a2-num  (if a2 (Double/parseDouble a2) 0)
            mid-num (if mid (Double/parseDouble mid) 0)
            fin-num (if fin (Double/parseDouble fin) 0)

            ;; Convert weight strings to fractions (weight / 100, default 0 if missing)
            a1w-num (if a1w (/ (Double/parseDouble a1w) 100) 0)
            a2w-num (if a2w (/ (Double/parseDouble a2w) 100) 0)
            midw-num (if midw (/ (Double/parseDouble midw) 100) 0)
            finw-num (if finw (/ (Double/parseDouble finw) 100) 0)

            ;; Compute weighted component scores (for total only)
            a1-weighted (* a1-num a1w-num)
            a2-weighted (* a2-num a2w-num)
            mid-weighted (* mid-num midw-num)
            fin-weighted (* fin-num finw-num)
            ;; The student's overall weighted total
            total (+ a1-weighted a2-weighted mid-weighted fin-weighted)]   

        ;; Update raw grade sums and counts (only if a grade is present).
        (when (> a1-num 0)
          (swap! nb-a1 inc)
          (swap! a1-sum #(+ % a1-num)))
        (when (> a2-num 0)
          (swap! nb-a2 inc)
          (swap! a2-sum #(+ % a2-num)))
        (when (> mid-num 0)
          (swap! nb-mid inc)
          (swap! mid-sum #(+ % mid-num)))
        (when (> fin-num 0)
          (swap! nb-fin inc)
          (swap! fin-sum #(+ % fin-num)))

        ;; Always add the overall weighted total.
        (swap! total-sum #(+ % total))

        ;; Print the student's row in the table.
        (println (format "%-10s | %-15s | %-8.2f | %-8.2f | %-8.2f | %-8.2f | %-10.2f"
                         (:id student)
                         (:first-name student)
                         a1-num
                         a2-num
                         mid-num
                         fin-num
                         total))))

    (println "----------------------------------------------------------------------------")

    ;; Compute averages using the raw grade sums and counts,
    ;; and the overall average of the weighted totals.
    (let [n (count students)
          avg-a1  (if (pos? @nb-a1) (/ @a1-sum @nb-a1) 0)
          avg-a2  (if (pos? @nb-a2) (/ @a2-sum @nb-a2) 0)
          avg-mid (if (pos? @nb-mid) (/ @mid-sum @nb-mid) 0)
          avg-fin (if (pos? @nb-fin) (/ @fin-sum @nb-fin) 0)
          avg-total (if (pos? n) (/ @total-sum n) 0)]
      (println (format "Averages:       A1: %.2f    A2: %.2f    MIDTERM: %.2f    FINAL: %.2f    TOTAL: %.2f"
                       avg-a1 avg-a2 avg-mid avg-fin avg-total)))
    (println "----------------------------------------------------------------------------"))

  (println (format "Total Students: %d" (count students)))
  (println "****************************************\n"))




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
             
             (if (= option "5")
               (option5 students)))))))


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


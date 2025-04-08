(ns a3
  (:require [clojure.string :as str])
  (:require [clojure.java.io :as io])
  ; this is where you would also include/require the compress module
  (:require [utils])  
)
  
; Display the menu and ask the user for the option
(defn showMenu
  []
  (println "\n\n*** Grade Processing Menu ***")
  (println     "-----------------------------\n")
  (println "1. List Names")
  (println "2. Process Something!")
  (println "3. Another option")
  (println "4. Run Test Code")
  (println "5. ...")
  (println "6. Exit")
  (do 
    (print "\nEnter an option? ") 
    (flush) 
    (read-line)))


; Replace the println expression with your own code (i.e. calling another function(s))
(defn option1
  [] ;parm(s) can be provided here, if needed
  (println "this is a place holder for option 1"))
    
    
; Replace the println expression with your own code
(defn option2
  [] ;parm(s) can be provided here, if needed
  (print "\nPlease enter something => ") 
  (flush)
  (let [sample-input (read-line)]
     (println "Process" sample-input)))

; Replace the println expression with your own code
(defn option3
  [] ;parm(s) can be provided here, if needed
)

; Replace the println expression with your own code
(defn option4
  [] ;parm(s) can be provided here, if needed
  (print "\nRunning the test code")
  (utils/run-test-code))



; If the menu selection is valid, call the relevant function to 
; process the selection
(defn processOption
  [option] ; other parm(s) can be provided here, if needed
  (if( = option "1")
     (option1)
     (if( = option "2")
        (option2)
        (if( = option "3")
           (option3)  ; other args(s) can be passed here, if needed
           (if( = option "4")
              (option4)   ; other args(s) can be passed here, if needed
              (println "Invalid Option, please try again"))))))


; Display the menu and get a menu item selection. Process the
; selection and then loop again to get the next menu selection
(defn menu
  [] ; parm(s) can be provided here, if needed
  (let [option (str/trim (showMenu))]
    (if (= option "6")
      (println "\nGood Bye\n")
      (do 
         (processOption option)
         (recur )))))   ; other args(s) can be passed here, if needed

; ------------------------------
; Run the program. You might want to prepare the data required for the mapping operations
; before you display the menu. You don't have to do this but it might make some things easier

; A grades.txt file will be provided in the *current directory*, during grading
; In general you need to read, process, and display the file content (grades.txt) 
; You may use Java's File class to check for existence. 

(menu) ; other args(s) can be passed here, if needed

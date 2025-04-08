(ns utils)
  
(defn mapify 
    ([grades] (mapify grades {}))
    ([grades gmap] 
    (if (< (count grades) 3) gmap
        (do
          (def res (assoc gmap (first grades) {:weight (nth grades 1), :grade (nth grades 2)}))
          (recur (drop 3 grades) res)))))

; do not use the following. just an example

(def sample-rec [40543437,"John","Doe","A1",6,95.5,"A2",6,100,"MIDTERM",20,80,"FINAL",78,70])

(defn run-test-code []
    (let [g (mapify (drop 3 sample-rec))]
        (println g)
        (letfn [(printg [gmap]
                    (doseq [k (sort (keys gmap))]
                        (println k ": " (get-in gmap [k :grade]))))]
            (printg g))))

(run-test-code)
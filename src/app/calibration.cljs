(ns app.calibration)
    
(def unit-table
  {"0" 100
   "1" 10
   "2" 1
   "3" 0.1
   "4" 0.01})
   

(defn convert-unit [v]
  (prn "conver-unit input: " v)
  (prn (str v))
  (prn (get unit-table (str v)))
  (let [u (get unit-table (str v))]
    (prn "convert unit " u)
    u))


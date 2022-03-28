(ns app.calibration)
    
(def unit-table
  {"0" 10
   "1" 1
   "2" 0.1
   "3" 0.01
   "4" 0.001
   "5" 0.0001})

(defn convert-unit [v]
  (prn "conver-unit input: " v)
  (prn (str v))
  (prn (get unit-table (str v)))
  (let [u (get unit-table (str v))]
    (prn "convert unit " u)
    u))


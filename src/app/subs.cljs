(ns app.subs
  (:require [re-frame.core :refer [reg-sub subscribe]]
            [app.calibration :refer [convert-unit]]
            [taoensso.timbre :refer [debug info error fatal]]))

(reg-sub
 :unit-number
 (fn [db _]
   (->> db
       :slider-val
       convert-unit)))


(reg-sub
 :testing
 (fn [db _]
   (prn "db " db)
   (:testing db)))

(reg-sub
 :unit-steps
 (fn [db _]
   (prn "unit-steps " db)
   (prn (:unit-steps db))
   (:unit-steps db)))


(reg-sub
 :calibration
 (fn [db _]
   (get db :calibration)))

(reg-sub
 :slider-val
 (fn [db _]
   (debug "sub-slider val " (get db :slider-val))
   (get db :slider-val)))


(reg-sub
 :connection-status
 (fn [db _]
   (get db :connection-status)))


;;;;;;;;;;;;;;;
(reg-sub
 :xml-file
 (fn [db _]
   (get db :xml-file)))

(reg-sub
 :xml-content
 (fn [db _]
   (get db :xml-content)))


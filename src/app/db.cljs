(ns app.db
  (:require [re-frame.core :as re-frame]))


(def default-db
  {:calibration {}
   :unit-steps 5
   :slider-val 1
   :connection-status "not connected"
   :unit-number 1})

(ns app.db
  (:require [re-frame.core :as re-frame]))


(def default-db
  {:calibration {}
   :unit-steps 4
   :slider-val 2
   :connection-status "not connected"
   :unit-number 1})

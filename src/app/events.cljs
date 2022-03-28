(ns app.events
  (:require [re-frame.core :refer [reg-event-db reg-event-fx inject-cofx path after]]
            [app.db :refer [default-db]]
            [app.calibration :refer [convert-unit]]
            [app.xmlfile :as xml]
            [taoensso.timbre :refer [debug info error fatal]]))

(defn- get-cur-xml-val [db group-marker item-name] (get-in db [:xml-content group-marker item-name]))
(defn- get-xml-path [db] (get-in db [:xml-file]))
(defn- get-xml-content [db] (get-in db [:xml-content]))
(defn- get-unit-number [db] (get-in db [:slider-val]))

(reg-event-db
 :init-db
 (fn [_ _]
   (let [db (merge {} default-db)]
    db)))

(reg-event-db
 :slider-val
 (fn [db [_ v]]
   (debug "slider-val " v)
   (assoc db :slider-val v)))

(reg-event-db
 :unit-number
 (fn [db [k v]]
   (assoc db k v)))


(reg-event-db
 :xml-file
 (fn [db [_ v]]
   (assoc db :xml-file v)))

(reg-event-db
 :xml-content
 (fn [db [_ v]]
   (assoc db :xml-content v)))

(reg-event-db
 :change-xml-val
 (fn [db [_ {:keys [marker item-name value]}]]
   (let [new-db (assoc-in db [:xml-content marker item-name] value)]
     (xml/save-file (get-xml-path new-db) (get-xml-content new-db))
     new-db)))
   
(reg-event-db
 :modify-xml-val
 (fn [db [_ {:keys [g-marker item-name dir] :as all}]]
   (debug "all " all)
      (let [fixed-sz 4
            op (condp = dir
                 :inc +
                 :dec -)
            cur-v (get-cur-xml-val db g-marker item-name)
            unit (get-unit-number db)
            new-v (.toFixed (op (js/parseFloat cur-v)
                                (convert-unit unit))
                            fixed-sz)
            new-db (assoc-in db [:xml-content g-marker item-name] new-v)]
        ;; (debug "cur-v " cur-v)
        ;; (debug "parser float " (js/parseFloat cur-v))
        ;; (debug "unit #####  " unit)
        ;; (debug "convert unit #####  " (convert-unit unit))
        (xml/save-file (get-xml-path new-db) (get-xml-content new-db))
        new-db)))
 

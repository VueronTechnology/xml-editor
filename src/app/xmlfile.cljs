(ns app.xmlfile
  (:require ["@tauri-apps/api/fs" :as fs]
            [clojure.string :as s]
            [re-frame.core :refer [dispatch]]
            [goog.string :as gstring]
            [goog.string.format]
            [app.funcs :as funcs]
            [taoensso.timbre :refer [debug info error fatal]]))

(defn split-nm-v [group]
  (into {} (map #(let [[_ nm v] (re-find #"\s*(\w+)\s*=\s*(-?\d+.?\d*)" %)]
                   [nm (js/parseFloat v)]) group)))

(defn refine-cal-data [content]
  (let [refined (->> (s/split content #"\r\n\r\n")
                     (map #(s/split-lines %))
                     (remove #(= 0 (count %))))]
    (let [d (for [group refined]
              (split-nm-v group))]
      (dispatch [:xml-content (zipmap (iterate inc 1) d)]))))

(defn make-content-format-group [group-content]
  (reduce (fn [acc [nm v]]
            (str acc nm " = " v "\r\n")) "" group-content))
  
(defn make-content-format [content]
  (let [group-str (for [[_ group] content]
                    (str (make-content-format-group group) "\r\n"))]
    (apply str group-str)))

           
(defn save-file [path content]
  (debug "save file" content)
  (-> (.writeFile fs (clj->js {:path path :contents (make-content-format content)}))
      (.then #(funcs/toast "saved file"))
      (.catch #(prn "save-file errro: " %))))


(defn read-file [path]
  (dispatch [:xml-file path])
  (-> (.readTextFile fs path)
      (.then #(refine-cal-data %))
      (.catch #(prn "read-file error: " %))))


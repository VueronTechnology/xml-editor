(ns app.xmlfile
  (:require ["@tauri-apps/api/fs" :as fs]
            [clojure.string :as s]
            [re-frame.core :refer [dispatch]]
            [goog.string :as gstring]
            [goog.string.format]
            [app.funcs :as funcs]
            [taoensso.timbre :refer [debug info error fatal]]))

(defn split-nm-v [group]
  (let [group-name (first group)
        data (rest group)]
  [group-name (into {} (map #(let [[_ nm v] (re-find #"\s*(\w+)\s*=\s*(-?\d+.?\d*)" %)]
                   [nm (js/parseFloat v)]) data))]))

(defn refine-cal-data [content]
  (let [refined (->> (s/split content #"\r\n\r\n")
                     (map #(s/split-lines %))
                     (remove #(= 0 (count %))))]
    (let [d (for [group refined]
              (split-nm-v group))]
      (dispatch [:xml-content (into {} d)]))))

(defn make-content-format-group [group-content]
  (reduce (fn [acc [nm v]]
            (str acc nm " = " v "\r\n")) "" group-content))
  
(defn make-content-format [content]
  (let [group-str (for [[group-name items] content]
                    (str group-name "\r\n" (make-content-format-group items) "\r\n"))]
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


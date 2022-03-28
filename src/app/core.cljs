(ns app.core
  (:require [reagent.dom :as r.dom]
            [re-frame.core :as rf :refer [dispatch dispatch-sync]]
            [app.views :as v]
            [cognitect.transit :as transit]
            [app.subs]
            [app.db]
            [app.events]))

(dispatch-sync [:init-db])  

(defn render []
  (r.dom/render [v/view-root]
                (js/document.getElementById "app")))

(defn ^:export init []
  (render))

(def ex "{\"abc-lidar\": {\":x\":0,
                            \":y\":0,
                            \":z\":0,
                            \":roll\":0,
                            \":pitch\":0,
                            \":yaw\":0},
           \"aa\": {\":x\":0,
                            \":y\":0,
                            \":z\":0,
                            \":roll\":0,
                            \":pitch\":0,
                            \":yaw\":0}}")


(def ex-json (.parse js/JSON ex))
(def reader (transit/reader :json))
(def data  (transit/read reader ex))
(prn "core - data " data)

;; (dispatch [:data data])

(defn ^:dev/after-load clear-cache-and-render!
  []
  ;; The `:dev/after-load` metadata causes this function to be called
  ;; after shadow-cljs hot-reloads code. We force a UI update by clearing
  ;; the Reframe subscription cache.
  (rf/clear-subscription-cache!)

  (render))

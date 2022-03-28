(ns app.ws
  (:require [cognitect.transit :as transit]
            [taoensso.timbre :refer [debug info error fatal]]
            [re-frame.core :refer [dispatch]]))


(def addr "ws://localhost:9000")

(def websocket (atom nil))


(def cmd-map
  {:req-cal-info {"flow" "req"
                  "cmd" "default-cal"
                  "data" nil}
   :req-apply-cal {"flow" "req"
                   "cmd" "apply-cal"
                   "data" nil}})

(defn map->json [v]
  (let [w (transit/writer :json-verbose)]
    (transit/write w v)))


(defn cmds [k data]
  (let [c (-> (get cmd-map k)
              (assoc "data" data)
              map->json)]
    c))



(defn update-data [packet]
  (js/console.log "packet " packet)
  (let [d (js->clj packet)
        cmd (get d "cmd")
        flow (get d "flow")
        contents-loc (get d "contents")]
  (condp = cmd
    "default-cal" (dispatch [:calibration (get d contents-loc)])
    (prn "unkown - " d))))


(defn prep-after-connection [sock]
  (reset! websocket sock)
  (set! (.-onmessage sock) (fn [event]
                            (prn "on message")
                             (js/console.log "received " (aget event "data"))
                             (update-data (.parse js/JSON (aget event "data")))))
  (set! (.-onopen sock) (fn [e]
                          (prn "on open")
                          (js/console.log "ready state " (.-readyState sock))
                          (dispatch [:connection-status "connected"])
                          (.send @websocket (cmds :req-cal-info nil))))
  (set! (.-onclose sock) (fn [e]
                           (dispatch [:connection-status "disconnected"])
                           (dispatch [:clear-calibration])
                           (if (.-wasClean e)
                             (prn "connection close - " (.-code e))
                             (prn "connection died"))))
  (set! (.-onerror sock) (fn [e]
                           (prn "on error")
                           ;; (let [closed 3]
                             ;; (if (= closed (.-readyState sock))
                           (js/console.log "ready state " (.-readyState sock))
                           (js/console.log e)
                           (js/console.log (.-message e)))))
                           
(defn connect []
  (let [sock (new js/WebSocket addr)]

    (if-not (nil? sock)
      (prep-after-connection sock)
      (prn "ws connection error"))))

(defn disconnect []
  (when (not (nil? @websocket))
    (do
      (.close @websocket)
      (reset! websocket nil))))



(defn send [cmd msg]
    (.send @websocket (cmds cmd msg)))

(defn apply-cal [msg]
  (send :req-apply-cal (map->json msg)))


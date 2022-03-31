(ns app.funcs
  (:require ["react-hot-toast" :as t]
            ["react-toastify" :as rt])) ;; :refer [toast ToastContainer]]))

(defn toast [msg]
  (.success rt/toast msg (clj->js {:position "bottom-right"
                 :autoClose 200
                 :hideProgressBar false
                 :closeOnClick true
                 :pauseOnHover true
                 :draggable true
                 :progress nil})))


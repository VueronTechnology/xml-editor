(ns app.views
  (:require [reagent.core :as r]
            [cognitect.transit :as transit]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [app.subs]
            ["@tauri-apps/api/dialog" :as dialog]
            ["@tauri-apps/api/notification" :as noti]
            [app.xmlfile :as xml]
            [app.funcs :as funcs]
            ["react-toastify" :as rt :refer [toast ToastContainer]]
            [taoensso.timbre :refer [debug info error fatal]]))

(defn get-val [id]
  (.-value (js/document.getElementById id)))

(defn remove-start-0 [v]
  (if (and (> (count v) 1)
           (.startsWith v "0"))
    (.substring v 1)
    v))

(defn avoid-empty [v]
  (if (> (count v) 0)
    (if (= 0 (-> (.trim v) count))
      0
      v)
    0))

(defn number-negative-filter [v]
  (let [[_ v] (re-find #"(^\-?[0-9]+\.?[0-9]*)" v)]
       (prn "VVV " v)
       v))


(defn filter-input-nums [v]
  (prn "in filter " v)
  (let [refined-v (-> v
                      clojure.string/trim
                      number-negative-filter
                      ;; remove-start-0
                      ;; avoid-empty
                      )]
    (prn "refined " refined-v)
    refined-v))


(defn text-input [{:keys [on-change props value on-save]}]
  (let [inner-val (r/atom value)]
    (fn []
      [:input (merge props
                     {
                      :type "text"
                      :value @inner-val
                      :on-blur #(on-save (-> % .-target .-value filter-input-nums))
                      :on-change #(let [v (-> % .-target .-value filter-input-nums)]
                                    (debug "inner val " v)
                                    (reset! inner-val v))
                      :on-key-down #(case (.-which %)
                                      13 (on-save (-> % .-target .-value filter-input-nums))
                                      27 nil ;;esc
                                      nil)})])))

(defn slider-selection [disp-name]
  (let [v @(subscribe [:slider-val])]
  [:div {:class "relative pt-1"}
   [:input {:type "range"
            :class "w-100" ;;"form-range appearance-none h-6 p-0 bg-transparent focus:outline-none focus:ring-0 focus:shadow-none"
            :min "0"
            :max "5"
            :step "1"
            :value v
            :on-change #(let [v (-> % .-target .-value)]
                          (dispatch [:slider-val v]))
            :id "slider"}]]))

(defn ui-slider []
  [:div {:class "flex flex-row gap-4"
         :key (gensym)}
   [:div {:class "flex inline relative "
          :key (gensym)}
    (slider-selection "unit")]
   (let [unit @(subscribe [:unit-number])]
     [:div {:class "flex inline relative w-8"
            :key (gensym)}
      [:div {:class "flex w-19"
             :key (gensym)}
       (str unit)]])])

(defn arrow-up [{:keys [marker nm onclick]}]
  [:div {:class "flex-initial w-8"
               :key (gensym)}
         [:button {:class "bg-grey-light hover:bg-grey items-center focus:outline-none focus:text-green-400"
                   :key (gensym)
                   :onClick onclick}
          [:svg {:key (gensym)
                 :xmlns "http://www.w3.org/2000/svg"
                 :class "h-8 w-8 text-black-400"
                 :fill "none"
                 :focusable false
                 :viewBox "0 0 24 24"
                 :stroke "currentColor"
                 :data-prefix "fas"
                 :stroke-width "2"}
           [:path {:key (gensym)
                   :stroke-linecap "round"
                   :stroke-linejoin"round"
                   :d "M9 11l3-3m0 0l3 3m-3-3v8m0-13a9 9 0 110 18 9 9 0 010-18z"}]]]])

(defn arrow-down [{:keys [g-marker nm onclick]}]
  [:div {:class "flex-initial w-8"
         :key (gensym)}
   [:button {:class "bg-grey-light hover:bg-grey items-center"
             :key (gensym)
             :onClick onclick}
    [:svg {:key (gensym)
           :xmlns "http://www.w3.org/2000/svg"
           :class "h-8 w-8"
           :fill "none"
           :viewBox "0 0 24 24"
           :stroke "currentColor"
           :stroke-width "2"}
     [:path {:key (gensym)
             :stroke-linecap "round"
             :stroke-linejoin "round"
             :d "M15 13l-3 3m0 0l-3-3m3 3V8m0 13a9 9 0 110-18 9 9 0 010 18z"}]]]])

(defn show-item [marker [nm v :as all]]
  [:div {:key (gensym)
         :class "flex flex-row w-full h-12"}
   [:div {:key (gensym)
          :class "flex-initial w-32 justify-center items-center"}
    nm]
   [:div {:key (gensym)
          :class "flex-initial inline-block w-20"}
    [text-input {:on-save #(dispatch [:change-xml-val {:marker marker
                                                       :item-name nm
                                                       :value (str %)}])
                 :props {:class "w-20 rounded-lg text-xs hover:border-rose-200"}
                 :value v
                 :key (gensym)}]]
   ;; (debug "marker nm v" marker ", " nm ", " v)
   (arrow-up {:g-marker marker :nm nm :v v :onclick #(dispatch [:modify-xml-val {:g-marker marker :item-name nm :dir :inc}])})
   (arrow-down {:g-marker marker :nm nm :v v :onclick #(dispatch [:modify-xml-val {:g-marker marker :item-name nm :dir :dec}])})])
                         

(defn show-item-group [[group content]]
  [:div {:class "flex flex-col "}
   (for [item content]
     (show-item group item))])

(defn show-xml-content [content]
  [:div {:class "flex flex-row gap-3 items-center"}
   (for [group content]
     [:div {:class "flex flex-col w-full"
            :key (gensym)}     
      (show-item-group group)])])
       

(defn open-file []
  [:div {:class "flex w-30"}
   [:button {:class "bg-blue-500 hover:bg-blue-800 text-white font-bold rounded-full px-10 py-2"
             :on-click (fn [e]
                         (let [f (.open dialog (clj->js {:multiple false}))]
                           (-> f
                               (.then #(xml/read-file %))
                               (.catch #(js/alert "file open error: " %)))))}
    "open xml"]])

(defn selected-file []
  (let [filename @(subscribe [:xml-file])]
    [:div (str "path: " filename)]))

(defn view-root []
  [:div {:class "flex flex-col"
         :key :main-div}
   [:> ToastContainer (clj->js {:position "bottom-right"
                                :autoClose 1000
                                :hideProgressBar false
                                :newestOnTop false
                                :closeOnClick true
                                :rtl false
                                :pauseOnFocusLoss false
                                :draggable true
                                :pauseOnHover true})]
   
   ;; [:button {:onClick #(funcs/toast "ASdf")} "asdfb"]
   [:div {:class "flex flex-row justify-center items-center gap-3"} 
    (open-file)
    (selected-file)
    (ui-slider)]
   (let [contents @(subscribe [:xml-content])]
     (show-xml-content contents))]
   )


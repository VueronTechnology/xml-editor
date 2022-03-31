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
    (do (debug (.substring v 1))
      (.substring v 1))
    v))

(defn avoid-empty [v]
  (if (> (count v) 0)
    (if (= 0 (-> (.trim v) count))
      0
      v)
    0))

(defn number-negative-filter [v]
  (if (= v "-")
    v
    (let [[_ v] (re-find #"(^\-?[0-9]+\.?[0-9]*)" v)]
      v)))

(defn filter-only-nums [v]
  (if (= v "0-")
    v
    (let [refined-v (-> v
                        str
                        clojure.string/trim
                        number-negative-filter)]
      refined-v)))

(defn onchange-fixed-sz [v]
  (let [exponent-sz 3
        fraction-sz 2
        new-v (js/parseFloat v)]
    (cond
      (= "-" v) v
      (= "" v) v
      (nil? v) v
      (.endsWith v ".") v
      :else (-> (.toFixed new-v fraction-sz)
                (rem (.pow js/Math 10 exponent-sz))))))

(defn fill-after-dot [v]
  (if (.endsWith v ".")
    (do
      (str v "0"))
    v))

(defn input-filter [v]
  (-> v
      filter-only-nums
      onchange-fixed-sz
      ))

(defn onblur-fixed-sz [v]
  (debug "on blur val: " v)
  (cond
    (= "" v) 0
    (.endsWith v ".") (str v "0")
    :else v))


(defn text-input [{:keys [on-change props value on-save]}]
  (let [inner-val (r/atom value)]
    (fn []
      [:input (merge props
                     {
                      :type "text"
                      :value @inner-val
                      :on-blur #(let [new-v  (-> % .-target .-value onblur-fixed-sz)]
                                  (reset! inner-val new-v)
                                  (on-save new-v))
                      :on-change #(let [new-v (-> % .-target .-value input-filter)]
                                   (reset! inner-val new-v))
                      :on-key-down #(case (.-which %)
                                      13 (on-save (-> % .-target .-value filter-only-nums))
                                      27 nil ;;esc
                                      nil)})])))

(defn slider-selection [disp-name]
  (let [v @(subscribe [:slider-val])]
  [:div {:class "relative pt-1"}
   [:input {:type "range"
            :class "w-100" ;;"form-range appearance-none h-6 p-0 bg-transparent focus:outline-none focus:ring-0 focus:shadow-none"
            :min "0"
            :max "4"
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
  [:div {:class "flex-initial w-8 self-center"
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
  [:div {:class "flex-initial w-8 self-center"
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
         :class "flex flex-row h-12 align-bottom"}
   ;; [:div {:key (gensym)
          ;; :class "flex-initial w-4 self-center"}
    ;; nm]
   [:div {:key (gensym)
          :class "flex-initial inline-block w-20 self-center"}
    [text-input {:on-save #(dispatch [:change-xml-val {:marker marker
                                                       :item-name nm
                                                       :value (str %)}])
                 ;; :on-change nil
                 ;; #(if-not (or (nil? %) (not (= "-" %)))
                               ;; (let [filtered (filter-only-nums %)]
                                 ;; filtered))
                                   ;; (-> (.toFixed (js/parseFloat filtered) fixed-sz)
                                       ;; (rem max-limit))))
                 :props {:class "w-20 rounded-lg text-xs hover:border-rose-200"}
                 :value v
                 :key (gensym)}]]
   (arrow-up {:g-marker marker :nm nm :v v :onclick #(dispatch [:modify-xml-val {:g-marker marker :item-name nm :dir :inc}])})
   (arrow-down {:g-marker marker :nm nm :v v :onclick #(dispatch [:modify-xml-val {:g-marker marker :item-name nm :dir :dec}])})])


(defn show-item-group [group-name content]
  [:div {:class "flex flex-col justify-center decoration-4"}
   [:div {:class "my-4 shadow-lg shadow-blue-100 "}
    [:p {:class ""}
     group-name]]
   (for [item content]
     (show-item group-name item))])

(defn show-xml-content [content]
  [:div {:class "flex flex-row align-top gap-4 justify-center items-center"}
   (let [names (-> content first second keys)]
     [:div {:class "flex flex-col justify-center gap-8"}
      [:div {:key (gensym)
             :class "flex-intitial w-4 self-center h-3"}
       ]
      (for [item-name names]
        [:div {:key (gensym)
               :class "flex-intitial w-4 self-center h-4"}
         item-name])])
   [:div {:class "flex overflow-scroll"}
    (for [[group-name item] content]
      [:div {:class "flex flex-col "
             :key (gensym)}
       (show-item-group group-name item)])]])


(defn open-file []
  [:div {:class "flex w-20"}
   [:button {:class "bg-blue-500 hover:bg-blue-800 text-white font-bold rounded-full px-4 py-2"
             :on-click (fn [e]
                         (let [f (.open dialog (clj->js {:multiple false}))]
                           (-> f
                               (.then #(xml/read-file %))
                               (.catch #(js/alert "file open error: " %)))))}
    "open"]])

(defn selected-file []
  (let [filename @(subscribe [:xml-file])]
    [:div {:class "flex"}
     [:p {:class "truncate px-4"}
      (if (empty? filename)
        "choose a xml file"
        (.pop (.split filename "/")))]]))

(defn view-root []
  [:div {:class "flex flex-col w-auto overflow-scroll"
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
   [:div {:class "flex flex-row justify-center items-center py-8 px-4 pa overflow-scroll "}
    (open-file)
    (ui-slider)]
   [:div {:class "flex justify-center items-center text-blue-400 mb-4"}
    (selected-file)]
   (let [contents @(subscribe [:xml-content])]
     (show-xml-content contents))]
   )


(ns offcourse.views.components.actions-panel
  (:require [rum.core :as rum]
            [cuerdas.core :as str]
            [shared.protocols.loggable :as log]))

(defn handler-button [button-text respond]
  [:li.actions-panel--item {:key button-text}
   [:a.actions-panel--link {:on-click #(respond)}
                           button-text]])

(rum/defc actions-panel [{:keys [user-name credentials] :as data}
                         respond]
  [:ul.actions-panel
    [:.actions-panel--item
      ; (handler-button "EP" #(respond [:switch-to :edit-profile]))
      #_[:.action-panel--search-container
        [:input.actions-panel--search {:placeholder "Enter your search"}]
        [:a.actions-panel--search-btn "Search"]]]
   (if credentials
     [:li.actions-panel--item {:key "new-course"}
      [:a.actions-panel--link {:href "/courses/new"}
                              "Create Course"]]
     (handler-button "Sign In" #(respond [:switch-to :auth])))
   (when credentials
     (handler-button user-name (partial respond [:sign-out])))])

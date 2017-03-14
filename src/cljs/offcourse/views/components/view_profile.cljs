(ns offcourse.views.components.view-profile
  (:require [rum.core :as rum]
            [cuerdas.core :as str]
            [offcourse.views.components.button :refer [button]]
            [shared.protocols.loggable :as log]
            [shared.protocols.specced :as sp]))

(rum/defc view-profile [user respond]
  [:.card {:data-card-type :wide}
    [:.card--section
      [:.card--row {:data-row-spaced true}
          [:h1.card--title {:data-title-indent true} (:user-name user)]
          [:.card--link {:on-click #(respond [:switch-to :edit-profile])}
                        "Edit Profile"]]]
    [:.card--section
      [:p.card--text {:data-text-indent true
                      :data-text-padded :large}
                     (map #(str "#" % " ") ["Phyton" "Database" "HTML" "CSS" "CLJS"])]
      [:p.card--text {:data-text-indent true
                      :data-text-padded :large}
                     "I'm a information architect in many ways"]
      [:p.card--text {:data-text-indent true}
        [:a.card--link {:data-link-type :strong}
                       "mywebsite.com"]]]
    [:.card--section
      (button {:button-text (str "Follow " (:user-name user))} #(log/log "Follow"))]])

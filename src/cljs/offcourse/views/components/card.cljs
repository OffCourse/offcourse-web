(ns offcourse.views.components.card
  (:require [offcourse.views.components.button :refer [button]]
            [offcourse.views.components.card-meta :refer [card-meta]]
            [offcourse.views.components.item-list :refer [item-list]]
            [offcourse.views.components.card-social :refer [card-social]]
            [rum.core :as rum]
            [shared.protocols.loggable :as log]))

(rum/defc card [{:keys [course-id goal course-slug checkpoints tags curator] :as course}
                respond]
 (let [{:keys [affordances course-url]} (meta course)
       {:keys [browsable? forkable? trackable? editable?]} affordances]
   (log/log "X" tags)
  [:.container
   [:.card {:on-click #()}
    [:.card--section
     [:a.card--title {:href (-> course meta :course-url)} goal]]
    [:.card--wrapper
     [:.card--section (card-meta course)]
     [:.card--section (item-list :todo checkpoints trackable? respond)]
     (when (or forkable? editable?)
      [:.card--section
       [:ul.card--row
        (when forkable? (button {:button-text "Fork"} #(respond [:fork course])))
        (when editable? (button {:button-text "Edit"} #(respond [:switch-to :edit-mode])))]])
     (when false
      [:.card--section (card-social)])]]]))

(rum/defc cards [{:keys [courses]} respond]
  [:.cards (map #(rum/with-key (card % respond) (:course-id %)) courses)])

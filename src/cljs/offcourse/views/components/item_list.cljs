(ns offcourse.views.components.item-list
  (:require [rum.core :as rum]
            [shared.protocols.specced :as sp]
            [shared.protocols.loggable :as log]))

(defn toggle-checkpoint [{:keys [complete?] :as checkpoint} respond]
  (respond [:update (assoc checkpoint :complete? (not complete?))]))

(rum/defc todo-list-item [{:keys [task complete? checkpoint-slug order] :as checkpoint} trackable? respond]
  (let [{:keys [selected checkpoint-url]} (meta checkpoint)]
    [:li.list--item {:data-selected selected
                     :data-item-type :todo}
     (when trackable? [:button.button
                       {:key :checkbox
                        :data-button-type :checkbox
                        :on-click #(toggle-checkpoint checkpoint respond)
                        :data-selected (boolean complete?)}])
     [:a {:key :title
          :href checkpoint-url} [:span task]]]))

(rum/defc item-list [list-type checkpoints trackable? respond]
  [:ul.list {:data-list-type (name list-type)}
    (case list-type
      :todo (map #(rum/with-key (todo-list-item % trackable? respond) (:checkpoint-id %)) checkpoints))])



(rum/defc edit-list-item < {:did-mount (fn [state]
                                           (let [checkpoint (rum/ref-node state "checkpoint-title")]
                                             (.focus checkpoint)))}
                           [checkpoint update-handler remove-handler create-handler]
  [:li.list--item
    [:.list--item-section {:on-focus #(create-handler)}
      [:input.list--course {:ref         "checkpoint-title" ;(str "checkpoint-title-" (or (:checkpoint-id checkpoint) "no-id--"))
                            :type        :text
                            :placeholder "Task Name"
                            :value        (or (:task checkpoint) "")
                            :on-change   (fn [event]
                                           (let [prop-value (.. event -target -value)
                                                 checkpoint (assoc-in checkpoint [:task] prop-value)]
                                             (update-handler checkpoint)))}]
      [:input.list--url    {:type        :text
                            :placeholder "Resource URL"
                            :value       (or (:resource-url checkpoint) "")
                            :on-change   (fn [event]
                                           (let [prop-value (.. event -target -value)
                                                 checkpoint (assoc-in checkpoint [:resource-url] prop-value)]
                                             (update-handler checkpoint)))}]]
    [:.list--item-section
      [:button.button {:key :remove-button
                       :data-button-type (name :icon)
                       :on-click #(remove-handler checkpoint)}
       [:img.button--icon {:src "/images/x.svg"}]]]])

(rum/defc edit-list [checkpoints update-handler remove-handler create-handler]
  [:ul.list {:data-list-type :edit}
    (map #(rum/with-key (edit-list-item % update-handler remove-handler (fn [])) (:checkpoint-id %)) checkpoints)
    (edit-list-item {} #() #() create-handler)])

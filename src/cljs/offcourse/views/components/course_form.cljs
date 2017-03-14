(ns offcourse.views.components.course-form
  (:require [rum.core :as rum]
            [offcourse.views.components.button :refer [button]]
            [offcourse.views.components.item-list :refer [edit-list edit-list-item]]
            [offcourse.views.components.dropdown :refer [dropdown]]
            [shared.protocols.specced :as sp]
            [shared.protocols.loggable :as log]
            [shared.models.course.index :as co]
            [shared.protocols.actionable :as ac]))

(defn update-prop [prop-name event atom]
  (let [prop-value (.. event -target -value)]
    (swap! atom #(assoc % prop-name prop-value))))

(defn update-checkpoint [course-atom course checkpoint]
  (swap! course-atom #(ac/perform course [:update checkpoint])))

(defn remove-checkpoint [course-atom course checkpoint]
  (swap! course-atom #(ac/perform course [:remove checkpoint])))

(defn create-checkpoint [course-atom course]
  (swap! course-atom #(ac/perform course [:add :new-checkpoint])))

(defn set-dropdown [dropdown-name dropdown-atom]
  (swap! dropdown-atom #(str dropdown-name)))

(rum/defcs course-form < (rum/local {} ::course)
                         (rum/local "" ::dropdown)
  [state {:keys [course]} respond]
  (let [course-atom   (::course state)
        old-course    course
        course        (merge course @course-atom)
        errors        (:cljs.spec/problems (sp/errors course))
        error-paths   (into #{} (map #(-> % :path first) errors))
        dirty?        (not= course old-course)
        valid?        (sp/valid? course)
        dropdown-atom (::dropdown state)
        dropdown?     @dropdown-atom]
    [:.card {:data-card-type :wide
             :on-click (when (not= dropdown? "") #(set-dropdown "" dropdown-atom))}
      [:.card--section
        [:h1.card--title {:data-title-indent true} "Save a course"]]
      [:.card--section
        [:.card--row {:data-row-spaced true
                      :data-row-padded :large}
          [:p.card--subtitle {:data-subtitle-indent true}
                             "Goal of the course"]
          [:p.card--link {:data-link-type :em
                          :on-click #(set-dropdown "title" dropdown-atom)}
                         "How to write a catchy goal to distinguish your course"]
          (dropdown {:title "Catchy Titles"
                     :text "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
                     :shown (= dropdown? "title")})]
        (when (contains? error-paths :goal) [:.card--error "This field is not correct yet"])
        [:input.form--field {:key       "title"
                             :type      :text
                             :placeholder "Goal"
                             :auto-focus true
                             :value     (or (:goal course) "")
                             :on-change #(update-prop :goal % course-atom)}]]
      [:.card--section
        [:.card--row {:data-row-spaced true
                      :data-row-padded :small}
          [:p.card--subtitle {:data-subtitle-indent true}
                             "Add or edit resources"]
          [:p.card--link {:data-link-type :em
                          :on-click #(set-dropdown "resources" dropdown-atom)}
                         "What are resources?"]
          (dropdown {:title "Resources"
                     :text "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
                     :shown (= dropdown? "resources")})]
        (edit-list (:checkpoints course)
                   #(update-checkpoint course-atom course %1)
                   #(remove-checkpoint course-atom course %1)
                   #(create-checkpoint course-atom course))]
     [:.card--section
       [:.card--row {:data-row-spaced true}
         [(when true
           (button {:button-text "Cancel"
                    :button-color "red"}
                   #(do
                     (respond [:switch-to :view-mode])
                     (respond [:go :home]))))]
         [:.card--row
           (when (and valid? dirty?)
             (button {:button-text "Save Course"}
                     #(respond [:create course])))
           (when false
             (button {:button-text "Publish Course"
                      :button-color "blue"}
                     #(respond [:update course])))]]]]))

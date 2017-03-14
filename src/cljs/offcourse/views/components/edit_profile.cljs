(ns offcourse.views.components.edit-profile
  (:require [rum.core :as rum]
            [cuerdas.core :as str]
            [offcourse.views.components.button :refer [button]]
            [offcourse.views.components.dropdown :refer [dropdown]]
            [shared.protocols.loggable :as log]
            [shared.protocols.specced :as sp]))

(defn button-title [string]
  (-> string
      str/humanize
      str/titleize))

(defn update-prop [prop-name event atom]
  (let [user-name (.. event -target -value)]
    (swap! atom #(assoc % prop-name user-name))))

(defn set-dropdown [dropdown-name dropdown-atom]
  (swap! dropdown-atom #(str dropdown-name)))

(rum/defc form-field [interest update-interest]
  [:input.form--field {:key interest
                       :data-field-type :half
                       :placeholder "Interest"
                       :value interest}])

(rum/defc form-social [user provider update-provider]
  [:.card--row {:key provider
                :data-row-padded :small}
    (button {:button-text (button-title (name provider))
             :button-color provider}
            #(log/log "Login with Linkedin"))
    [:.card--title {:data-title-disabled (if (get user :linkedin) false true)
                    :data-title-indent true}
      (or (:linkedin user)) (str "Add your " (button-title (name provider)) " account")]])

(rum/defcs edit-profile < (rum/local {} ::user)
                          (rum/local "" ::dropdown)
  [state user respond]
  (let [user-atom (::user state)
        user (merge user @user-atom)
        valid? (sp/valid? user)
        dropdown-atom (::dropdown state)
        dropdown?     @dropdown-atom]
    [:.card {:data-card-type :wide
             :on-click (when (not= dropdown? "") #(set-dropdown "" dropdown-atom))}
      [:.card--section
        [:.card--row {:data-row-spaced true}
          [:h1.card--title {:data-title-indent true} "Edit your Profile"]
          [:.card--link {:on-click #(respond [:switch-to :view-profile])} "View Profile"]]]

      [:.card--section
        [:.card--row {:data-row-padded :small}
          [:p.card--subtitle {:data-subtitle-indent true} "How would like like to be called"]]
        [:input.form--field {:placeholder "Username"
                             :value (:user-name user)
                             :auto-focus true
                             :on-change #(update-prop :user-name % user-atom)
                             :data-field-margin true}]
        [:.card--row {:data-row-padded :small}
          [:p.card--subtitle {:data-subtitle-indent true} "Where can we reach you"]]
        [:input.form--field {:placeholder "E-mail"
                             :value (:email user)
                             :on-change #(update-prop :email % user-atom)}]]

      [:.card--section
        [:.card--row {:data-row-padded :small}
          [:p.card--subtitle {:data-subtitle-indent true}
                             "What would you like to learn"]]
        [:.card--row {:data-row-wrap true}
          (map #(rum/with-key (form-field % (fn [] ()) %) %) ["Phyton" "Database" "HTML" "CSS" "CLJS"])]]

      [:.card--section
        [:.card--row {:data-row-spaced true
                      :data-row-padded :large}
          [:p.card--subtitle {:data-subtitle-indent true}
                             "Add your accounts"]
          [:.card--link {:data-link-type :em
                         :on-click #(set-dropdown "data" dropdown-atom)}
                        "What Offcourse will do with your accounts"]
          (dropdown {:title "Datapolicy"
                     :text "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
                     :shown (= dropdown? "data")})]
        (map #(rum/with-key (form-social user % (fn [] ())) %) [:github :twitter :linkedin])]

      [:.card--section
        [:.card--row {:data-row-padded :small}
          [:p.card--subtitle {:data-subtitle-indent true} "Some cool information about you"]]
        [:input.form--field {:placeholder "Information"
                             :value (:description user)
                             :on-change #(update-prop :description % user-atom)}]]

      [:.card--section
        [:.card--row {:data-row-padded :small}
          [:p.card--subtitle {:data-subtitle-indent true} "What place can we visit to get to know you better"]]
        [:input.form--field {:placeholder "Website URL"
                             :value (:website user)
                             :on-change #(update-prop :website % user-atom)}]]

      (when true ;valid?
        [:.card--section
          [:.card--row {:data-row-spaced true}
            (button {:button-text "Cancel"
                     :button-color "red"}
                    #(respond [:switch-to :view-mode]))
            (button {:button-text "Save Changes"}
                    #(log/log "Update user"))]])]))

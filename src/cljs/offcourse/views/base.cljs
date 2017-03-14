(ns offcourse.views.base
  (:require [clojure.set :as set]
            [offcourse.views.containers.app :refer [app]]
            [offcourse.views.containers.menubar :refer [menubar]]
            [offcourse.views.components.flash :refer [flash]]
            [offcourse.views.containers.overlay :refer [overlay]]
            [offcourse.views.components.actions-panel :refer [actions-panel]]
            [offcourse.views.components.logo :refer [logo]]
            [offcourse.views.components.auth-form :refer [auth-form]]
            [offcourse.views.components.user-form :refer [user-form]]
            [offcourse.views.components.edit-profile :refer [edit-profile]]
            [offcourse.views.components.view-profile :refer [view-profile]]
            [offcourse.views.components.course-form :refer [course-form]]
            [plumbing.core :refer-macros [fnk]]
            [shared.protocols.loggable :as log]
            [shared.protocols.specced :as sp]))

(def notification {:title "Thank you for signing up!"
                   :link  "You can edit your profile here"
                   :color "yellow"})

(def graph
  {:base-actions   (fnk [] #{:go :authenticate :sign-out :switch-to :sign-up})
   :actions        (fnk [base-actions view-actions] (set/union base-actions view-actions))
   :container      (fnk [] app)
   :viewmodel      (fnk [appstate] (-> appstate :viewmodel))
   :app-mode       (fnk [appstate] (-> appstate :app-mode))
   :user           (fnk [appstate] (-> appstate :user))
   :respond        (fnk [responder actions]
                        (fn [[action-type :as action]]
                          (if (contains? actions action-type)
                            (responder [:requested action])
                            (log/error action-type (str "Invalid action for this view")))))
   :logo           (fnk [[:appstate site-title] respond]
                        (logo {:site-title site-title} respond))
   :actions-panel  (fnk [user respond] (actions-panel user respond))
   :menubar        (fnk [logo actions-panel appstate]
                        (menubar logo actions-panel))
   :notification   (fnk [appstate] notification)
   :flash          (fnk [notification respond]
                        (when false (flash notification respond)))
   :base-overlays  (fnk [user respond]
                        {:auth (overlay (auth-form respond) respond)
                         :new-user (overlay (user-form user respond) respond)
                         :edit-profile (overlay (edit-profile user respond) respond)
                         :view-profile (overlay (view-profile user respond) respond)
                         :curate-mode (overlay (course-form nil respond) respond)})
   :overlays       (fnk [base-overlays view-overlays]
                        (merge base-overlays view-overlays))
   :overlay        (fnk [app-mode overlays user respond]
                        (when-let [overlay (get overlays app-mode)]
                          overlay))})

(ns offcourse.views.new-course
  (:require [offcourse.views.components.course-form :refer [course-form]]
            [offcourse.views.containers.overlay :refer [overlay]]
            [plumbing.core :refer-macros [fnk]]
            [shared.protocols.loggable :as log]
            [shared.models.course.index :as course-model]
            [shared.models.checkpoint.index :as cp-model]))

(def graph
  {:view-actions    (fnk [] #{:create :update})
   :view-overlays   (fnk [] {})
   :new-course      (fnk [appstate]
                         (let [curator (-> appstate :user :user-name)]
                           (when curator
                             (-> {:curator curator}
                                 course-model/map->Course
                                 (with-meta {:spec :offcourse/new-course})))))
   :main            (fnk [new-course respond]
                         (if new-course
                           (course-form {:course new-course} respond)
                           "You are not logged in"))})

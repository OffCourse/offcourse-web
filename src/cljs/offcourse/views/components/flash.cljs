(ns offcourse.views.components.flash
  (:require [rum.core :as rum]))

(rum/defc flash [notification respond]
  [:.notifybar {:data-notify-color (:color notification)}
    [:.notifybar--section
      [:.notifybar--subtitle (:title notification)]
      (when (:link notification)
        [:a.notifybar--link (:link notification)])]
    [:.notifybar--section
       [:.notifybar--link {:data-link-type :strong}
                          "Close"]]])

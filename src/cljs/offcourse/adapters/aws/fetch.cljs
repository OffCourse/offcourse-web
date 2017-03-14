(ns offcourse.adapters.aws.fetch
  (:require [ajax.core :refer [POST]]
            [cljs.core.async :refer [chan]]
            [clojure.walk :as walk]
            [shared.models.event.index :as event]
            [shared.protocols.specced :as sp]
            [shared.specs.helpers :as sh]
            [shared.protocols.loggable :as log])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn handle-response [name payload]
  (walk/keywordize-keys payload))

(defn fetch [{:keys [name endpoint]} query]
  (log/log "X" (.stringify js/JSON (clj->js query)))
  (let [c (chan)
        auth-token ""]
    (POST endpoint
        {:headers "" #_{:Authorization ""}
         :params query
         :format :json
         :handler #(go (>! c (handle-response name %)))})
    c))

(ns offcourse.command.react
  (:require [shared.protocols.eventful :as ef]
            [shared.protocols.specced :as sp]
            [shared.protocols.queryable :as qa]
            [shared.models.payload.index :as payload]
            [shared.protocols.convertible :as cv]
            [cljs.core.async :as async]
            [shared.protocols.loggable :as log]
            [shared.protocols.actionable :as ac])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defmulti react (fn [_ [_ action]] (sp/resolve action)))

(defmethod react :authenticate [])
(defmethod react :go [])
(defmethod react :sign-out [])

(defmethod react :sign-in
  [{:keys [component-name adapter] :as service} [_ action :as event]]
  (go
    (let [auth-token (some-> event meta :credentials :auth-token)
          request (ac/request adapter (with-meta action {:auth-token auth-token}))
          {:keys [accepted denied]} (async/<! request)
          not-found true
          failed true]
      (when accepted  (ef/respond service [:signed-in (-> accepted payload/create)]))
      (when not-found (ef/respond service [:not-found request]))
      (when failed    (ef/respond service [:failed request])))))

      ; It feels rational like this, three different actions for the three cases
      ; 1. accepted: It's accepted and a payload is created and send to the conductor
      ; 2. not-found: It's processed by the API but did not turn up a user, request is send
      ;    back for introspection. The conductor will followup with the creation of a new identity
      ;    and trigger the action [:switch-to sign-up].
      ; 3. failed: Something went wrong with the connection, the API was not reached.
      ;    Flash should be triggered to notify the user that this catastrophe has happened.

(defmethod react :sign-up
  [{:keys [component-name adapter] :as service} [_ action :as event]])
      ; Todo: Send the full identity and the auth-profile])

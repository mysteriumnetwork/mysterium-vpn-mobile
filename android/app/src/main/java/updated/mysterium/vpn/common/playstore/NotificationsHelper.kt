package updated.mysterium.vpn.common.playstore

import updated.mysterium.vpn.model.pushy.PushyTopic

interface NotificationsHelper {

    fun listen()

    fun register(onRegisteredAction: () -> Unit)

    fun subscribe(pushyTopic: String)

    fun subscribe(pushyTopic: PushyTopic)

    fun unsubscribe(pushyTopic: PushyTopic)

}

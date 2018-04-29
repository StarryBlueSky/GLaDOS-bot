package jp.nephy.glados.feature.listener.audit.channel

import jp.nephy.glados.component.helper.displayName
import jp.nephy.glados.feature.ListenerFeature
import net.dv8tion.jda.core.events.channel.priv.PrivateChannelCreateEvent
import net.dv8tion.jda.core.events.channel.priv.PrivateChannelDeleteEvent


class PrivateChannel: ListenerFeature() {
    override fun onPrivateChannelCreate(event: PrivateChannelCreateEvent) {
        helper.slackLog(event) { "${event.user.displayName} とのDMが開始されました." }
    }

    override fun onPrivateChannelDelete(event: PrivateChannelDeleteEvent) {
        helper.slackLog(event) { "${event.user.displayName} とのDMを終了されました." }
    }
}

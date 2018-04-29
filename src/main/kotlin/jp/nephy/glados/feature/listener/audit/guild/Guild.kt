package jp.nephy.glados.feature.listener.audit.guild

import jp.nephy.glados.component.helper.Color
import jp.nephy.glados.component.helper.displayName
import jp.nephy.glados.feature.ListenerFeature
import net.dv8tion.jda.core.events.guild.*


class Guild: ListenerFeature() {
    override fun onGuildJoin(event: GuildJoinEvent) {
        helper.slackLog(event) { "サーバ `${event.guild.name}` に参加しました." }
    }

    override fun onUnavailableGuildJoined(event: UnavailableGuildJoinedEvent) {
        helper.slackLog(event) { "サーバ (ID: ${event.guildId}) に参加しましたが, このサーバは利用不可能です." }
    }

    override fun onGuildLeave(event: GuildLeaveEvent) {
        helper.slackLog(event) { "サーバ `${event.guild.name}` から退出しました." }
    }

    override fun onGuildBan(event: GuildBanEvent) {
        helper.messageLog(event, "BAN", Color.Bad) { "${event.user.displayName} (${event.user.asMention}) がBANされました。" }
    }

    override fun onGuildUnban(event: GuildUnbanEvent) {
        helper.messageLog(event, "BAN解除", Color.Good) { "${event.user.displayName} (${event.user.asMention}) がBAN解除されました。" }
    }

    override fun onGuildAvailable(event: GuildAvailableEvent) {
        helper.slackLog(event) { "サーバ `${event.guild.name}` が利用可能になりました." }
    }

    override fun onGuildUnavailable(event: GuildUnavailableEvent) {
        helper.slackLog(event) { "サーバ `${event.guild.name}` が利用不可能になりました." }
    }
}

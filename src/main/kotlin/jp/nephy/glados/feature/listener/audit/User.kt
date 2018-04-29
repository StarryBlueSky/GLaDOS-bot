package jp.nephy.glados.feature.listener.audit

import jp.nephy.glados.GLaDOS
import jp.nephy.glados.component.helper.displayName
import jp.nephy.glados.component.helper.isSelf
import jp.nephy.glados.component.helper.toDeltaMilliSecondString
import jp.nephy.glados.feature.ListenerFeature
import net.dv8tion.jda.client.events.relationship.UserBlockedEvent
import net.dv8tion.jda.client.events.relationship.UserUnblockedEvent
import net.dv8tion.jda.core.OnlineStatus
import net.dv8tion.jda.core.events.user.UserTypingEvent
import net.dv8tion.jda.core.events.user.update.*
import java.util.*


class User(bot: GLaDOS): ListenerFeature(bot) {
    override fun onUserTyping(event: UserTypingEvent) {
        if (event.user.isSelf || event.user.isBot) {
            return
        }

        helper.slackLog(event) { "#${event.channel.name} で現在入力中です..." }
    }

    override fun onUserUpdateName(event: UserUpdateNameEvent) {
        if (event.user.isSelf) {
            return
        }

        helper.slackLog(event) { "ユーザ名を変更しました. 元のユーザ名: @${event.oldName}#${event.entity.discriminator}" }
    }
    override fun onUserUpdateDiscriminator(event: UserUpdateDiscriminatorEvent) {
        if (event.user.isSelf) {
            return
        }

        helper.slackLog(event) { "識別子を変更しました. 元のユーザ名: @${event.entity.name}#${event.oldDiscriminator}" }
    }

    private val onlineTimeCache = mutableMapOf<Long, Long>()
    private val afkTimeCache = mutableMapOf<Long, Long>()
    override fun onUserUpdateOnlineStatus(event: UserUpdateOnlineStatusEvent) {
        val (previous, current) = event.oldOnlineStatus to event.newOnlineStatus
        val onlineCount = event.guild.members.count { it.onlineStatus != OnlineStatus.OFFLINE }
        val afkTime = afkTimeCache[event.user.idLong + event.guild.idLong].toDeltaMilliSecondString()
        val onlineTime = onlineTimeCache[event.user.idLong + event.guild.idLong].toDeltaMilliSecondString()

        if (current == OnlineStatus.IDLE) {
            afkTimeCache[event.user.idLong + event.guild.idLong] = Date().time
        } else if (previous == OnlineStatus.OFFLINE) {
            onlineTimeCache[event.user.idLong + event.guild.idLong] = Date().time
        }

        val text = when (previous to current) {
            OnlineStatus.ONLINE to OnlineStatus.DO_NOT_DISTURB -> "非通知モードを有効にしました."
            OnlineStatus.ONLINE to OnlineStatus.IDLE -> "AFKになりました."
            OnlineStatus.ONLINE to OnlineStatus.OFFLINE -> "オフラインになりました($onlineTime). 現在のオンライン数: $onlineCount"

            OnlineStatus.DO_NOT_DISTURB to OnlineStatus.ONLINE -> "非通知モードを無効にしました."
            OnlineStatus.DO_NOT_DISTURB to OnlineStatus.IDLE -> "AFKになりました."
            OnlineStatus.DO_NOT_DISTURB to OnlineStatus.OFFLINE -> "オフラインになりました($onlineTime). 現在のオンライン数: $onlineCount"

            OnlineStatus.IDLE to OnlineStatus.ONLINE -> "戻りました($afkTime)."
            OnlineStatus.IDLE to OnlineStatus.DO_NOT_DISTURB -> "非通知モードを有効にしました."
            OnlineStatus.IDLE to OnlineStatus.OFFLINE -> "オフラインになりました($onlineTime). 現在のオンライン数: $onlineCount"

            OnlineStatus.OFFLINE to OnlineStatus.ONLINE -> "オンラインになりました. 現在のオンライン数: $onlineCount"
            OnlineStatus.OFFLINE to OnlineStatus.DO_NOT_DISTURB -> "非通知モードでオンラインになりました. 現在のオンライン数: $onlineCount"
            OnlineStatus.OFFLINE to OnlineStatus.IDLE -> "AFK状態でオンラインになりました. 現在のオンライン数: $onlineCount"

            else -> return
        }

        helper.slackLog(event) { text }
    }

    private val gameCache = mutableMapOf<Long, Long>()
    override fun onUserUpdateGame(event: UserUpdateGameEvent) {
        if (event.user.isBot) {
            return
        }

        val (previous, current) = event.oldGame to event.newGame

        val text = when {
            previous?.name == null && current?.name != null -> {
                gameCache[event.user.idLong + event.guild.idLong] = Date().time
                "\"${current.name}\" を始めました."
            }
            previous?.name != null && current?.name == null -> {
                val time = gameCache[event.user.idLong + event.guild.idLong].toDeltaMilliSecondString()
                "\"${previous.name}\" をやめました($time)."
            }
            previous?.name != null && current?.name != null && previous.name != current.name -> {
                val time = gameCache[event.user.idLong + event.guild.idLong].toDeltaMilliSecondString()
                gameCache[event.user.idLong + event.guild.idLong] = Date().time
                "\"${previous.name}\" をやめ, \"${current.name}\" を始めました($time)."
            }
            else -> return
        }

        helper.slackLog(event) { text }
    }

    override fun onUserUpdateAvatar(event: UserUpdateAvatarEvent) {
        helper.slackLog(event) { "アイコンを変更しました. 元のアイコン: ${event.oldAvatarUrl}" }
    }

    override fun onUserBlocked(event: UserBlockedEvent) {
        helper.slackLog(event) { "ユーザ `${event.user.displayName}` をブロックしました." }
    }
    override fun onUserUnblocked(event: UserUnblockedEvent) {
        helper.slackLog(event) { "ユーザ `${event.user.displayName}` をブロック解除しました." }
    }
}

package jp.nephy.glados.feature.listener.audit

import jp.nephy.glados.GLaDOS
import jp.nephy.glados.component.helper.displayName
import jp.nephy.glados.component.helper.fullName
import jp.nephy.glados.component.helper.isSelf
import jp.nephy.glados.component.helper.toDeltaMilliSecondString
import jp.nephy.glados.feature.ListenerFeature
import net.dv8tion.jda.client.events.relationship.UserBlockedEvent
import net.dv8tion.jda.client.events.relationship.UserUnblockedEvent
import net.dv8tion.jda.core.OnlineStatus
import net.dv8tion.jda.core.events.user.*
import java.util.*


class User(bot: GLaDOS): ListenerFeature(bot) {
    override fun onUserTyping(event: UserTypingEvent) {
        if (event.user.isSelf) {
            return
        }

        bot.logger.debug { "[#${event.channel.name}] ${event.member.fullName}:\n[現在入力中です...]" }
    }

    override fun onUserNameUpdate(event: UserNameUpdateEvent) {
        if (event.user.isSelf) {
            return
        }

        bot.logger.info { "${event.user.displayName}: ユーザ名を変更しました. 元のユーザ名: @${event.oldName}#${event.oldDiscriminator}" }
    }

    private val statusCache = mutableMapOf<Long, Long>()
    override fun onUserOnlineStatusUpdate(event: UserOnlineStatusUpdateEvent) {
        if (event.user.idLong == 192713931876204545) {
            return
        }

        val previous = event.previousOnlineStatus
        val current = event.currentOnlineStatus
        if (previous == OnlineStatus.OFFLINE) {
            statusCache[event.user.idLong] = Date().time
        }

        val text = if (current == OnlineStatus.IDLE) {
            when (previous) {
                OnlineStatus.DO_NOT_DISTURB -> "非通知からAFK状態になりました."
                OnlineStatus.OFFLINE -> "AFK状態でオンラインになりました."
                OnlineStatus.ONLINE -> "AFK状態になりました.".run {
                    val time = statusCache[event.user.idLong].toDeltaMilliSecondString()
                    if (time != null) {
                        "$this オンライン時間: $time"
                    } else {
                        this
                    }
                }
                else -> null
            }
        } else if (previous == OnlineStatus.IDLE && current == OnlineStatus.ONLINE) {
            "戻りました."
        } else if (current == OnlineStatus.DO_NOT_DISTURB) {
            when (previous) {
                OnlineStatus.IDLE -> "AFK状態から非通知モードに切り替えました."
                OnlineStatus.OFFLINE -> "非通知モードでオンラインになりました."
                OnlineStatus.ONLINE -> "非通知モードに切り替えました."
                else -> null
            }
        } else if (previous == OnlineStatus.DO_NOT_DISTURB && current == OnlineStatus.ONLINE) {
            "非通知モードを解除しました."
        } else if (previous == OnlineStatus.OFFLINE && current == OnlineStatus.ONLINE) {
            "オンラインになりました. 現在のオンライン数: ${event.guild.members.count { it.onlineStatus != OnlineStatus.OFFLINE }}"
        } else if (current == OnlineStatus.OFFLINE) {
            "オフラインになりました. 現在のオンライン数: ${event.guild.members.count { it.onlineStatus != OnlineStatus.OFFLINE }}".run {
                val time = statusCache[event.user.idLong].toDeltaMilliSecondString()
                if (time != null) {
                    "$this オンライン時間: $time"
                } else {
                    this
                }
            }
        } else {
            null
        }

        if (text != null) {
            helper.slackLog(event) { text }
        }
    }

    private val gameCache = mutableMapOf<Long, Long>()
    override fun onUserGameUpdate(event: UserGameUpdateEvent) {
        if (event.user.isBot) {
            return
        }

        val previous = event.previousGame
        val current = event.currentGame

        val text = if (previous?.name == null && current?.name != null) {
            gameCache[event.user.idLong] = Date().time
            "\"${current.name}\" を始めました."
        } else if (previous?.name != null && current?.name == null) {
            "\"${previous.name}\" をやめました.".run {
                val time = gameCache[event.user.idLong].toDeltaMilliSecondString()
                if (time != null) {
                    "$this プレイ時間: $time"
                } else {
                    this
                }
            }
        } else if (previous?.name != null && current?.name != null && previous.name != current.name) {
            "\"${previous.name}\" をやめ, \"${current.name}\" を始めました.".run {
                val time = gameCache[event.user.idLong].toDeltaMilliSecondString()
                gameCache[event.user.idLong] = Date().time
                if (time != null) {
                    "$this プレイ時間: $time"
                } else {
                    this
                }
            }
        } else {
            null
        }

        if (text != null) {
            helper.slackLog(event) { text }
        }
    }

    override fun onUserAvatarUpdate(event: UserAvatarUpdateEvent) {
        bot.logger.info { "${event.user.displayName}: アイコンを変更しました. 元のアイコン: ${event.previousAvatarUrl}, 現在のアイコン: ${event.user.avatarUrl}" }
    }

    override fun onUserBlocked(event: UserBlockedEvent) {
        bot.logger.info { "ユーザ `${event.user.displayName}` をブロックしました." }
    }
    override fun onUserUnblocked(event: UserUnblockedEvent) {
        bot.logger.info { "ユーザ `${event.user.displayName}` をブロック解除しました." }
    }
}

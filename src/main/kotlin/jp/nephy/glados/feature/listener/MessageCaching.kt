package jp.nephy.glados.feature.listener

import jp.nephy.glados.GLaDOS
import jp.nephy.glados.component.helper.getMessages
import jp.nephy.glados.component.helper.isSelf
import jp.nephy.glados.component.helper.tmpFile
import jp.nephy.glados.feature.ListenerFeature
import jp.nephy.glados.logger
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.events.ReadyEvent
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import java.nio.file.Files
import kotlin.concurrent.thread


class MessageCaching: ListenerFeature() {
    override fun onReady(event: ReadyEvent) {
        thread(name = "Initial Messages Caching") {
            event.jda.guilds.forEach { guild ->
                guild.textChannels.filter { it.hasLatestMessage() }.forEach { channel ->
                    val self = channel.members.find { it.user.isSelf }
                    if (self != null && self.hasPermission(Permission.MESSAGE_HISTORY)) {
                        logger.info { "テキストチャンネル: #${channel.name} の取得を開始します." }
                        channel.getMessages(300).forEach { m ->
                            bot.messageCacheManager.add(m)
                            m.attachments.forEach {
                                val file = tmpFile("attachments", m.guild.id, m.channel.id, "${m.id}_${it.idLong}_${it.fileName}")
                                if (! Files.exists(file.toPath())) {
                                    it.download(file)
                                }
                            }
                        }
                    } else {
                        logger.warn { "テキストチャンネル: #${channel.name} (${guild.name}) に対するメッセージ履歴閲覧権限がありません." }
                    }
                }
            }

            logger.info { "テキストチャンネルのキャッシュが完了しました." }
        }
    }

    override fun onMessageReceived(event: MessageReceivedEvent) {
        bot.messageCacheManager.add(event)
    }
}

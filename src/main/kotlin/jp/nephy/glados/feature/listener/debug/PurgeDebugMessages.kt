package jp.nephy.glados.feature.listener.debug

import jp.nephy.glados.component.helper.getMessages
import jp.nephy.glados.component.helper.isSelf
import jp.nephy.glados.feature.ListenerFeature
import jp.nephy.glados.logger
import net.dv8tion.jda.core.entities.MessageType
import net.dv8tion.jda.core.events.ReadyEvent


class PurgeDebugMessages: ListenerFeature() {
    override fun onReady(event: ReadyEvent) {
        if (bot.isDebugMode) {
            logger.info { "メッセージのクリーンアップを開始します." }

            event.jda.guilds.forEach {
                val config = bot.config.getGuildConfig(it)
                if (config.textChannel.bot == null) {
                    return@forEach
                }
                val channel = it.getTextChannelById(config.textChannel.bot)

                if (channel.hasLatestMessage()) {
                    val messages = channel.getMessages(100).filter { it.author.isSelf && it.type != MessageType.GUILD_MEMBER_JOIN }

                    if (messages.isEmpty()) {
                        logger.debug { "テキストチャンネル: #${channel.name}(${channel.guild.name}) は空でした." }
                    } else {
                        if (messages.size == 1) {
                            channel.deleteMessageById(messages.first().idLong)
                        } else {
                            channel.deleteMessages(messages)
                        }.queue {
                            logger.debug { "テキストチャンネル: #${channel.name}(${channel.guild.name}) のクリーンアップが完了しました." }
                        }
                    }
                }
            }
        }
    }
}

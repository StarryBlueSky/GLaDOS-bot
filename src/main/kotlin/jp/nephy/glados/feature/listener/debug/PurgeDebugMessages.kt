package jp.nephy.glados.feature.listener.debug

import jp.nephy.glados.GLaDOS
import jp.nephy.glados.component.helper.getMessages
import jp.nephy.glados.component.helper.isSelf
import jp.nephy.glados.feature.ListenerFeature
import net.dv8tion.jda.core.entities.MessageType
import net.dv8tion.jda.core.events.ReadyEvent


class PurgeDebugMessages(bot: GLaDOS): ListenerFeature(bot) {
    override fun onReady(event: ReadyEvent) {
        if (GLaDOS.debug) {
            bot.logger.info { "メッセージのクリーンアップを開始します." }

            event.jda.guilds.forEach {
                val config = bot.config.getGuildConfig(it)
                if (config.textChannel.bot == null) {
                    return@forEach
                }
                val channel = it.getTextChannelById(config.textChannel.bot)

                if (channel.hasLatestMessage()) {
                    val messages = channel.getMessages(100).filter { it.author.isSelf && it.type != MessageType.GUILD_MEMBER_JOIN }

                    if (messages.isEmpty()) {
                        bot.logger.debug { "テキストチャンネル: #${channel.name}(${channel.guild.name}) は空でした." }
                    } else {
                        if (messages.size == 1) {
                            channel.deleteMessageById(messages.first().idLong)
                        } else {
                            channel.deleteMessages(messages)
                        }.queue {
                            bot.logger.debug { "テキストチャンネル: #${channel.name}(${channel.guild.name}) のクリーンアップが完了しました." }
                        }
                    }
                }
            }
        }
    }
}

package jp.nephy.glados.feature.command

import com.jagrosh.jdautilities.command.CommandEvent
import jp.nephy.glados.GLaDOS
import jp.nephy.glados.component.helper.deleteQueue
import jp.nephy.glados.feature.CommandFeature
import java.util.concurrent.TimeUnit


class Purge(bot: GLaDOS): CommandFeature(bot) {
    init {
        name = "purge"
        help = "指定されたチャンネルのメッセージをクリーンアップします。"

        isAdminCommand = true
    }

    override fun executeCommand(event: CommandEvent) {
        event.reply("クリーンアップしています...") {
            try {
                val messages = event.textChannel.iterableHistory.cache(false).take(100).filter { it.author.isBot }
                if (messages.isNotEmpty()) {
                    if (messages.size == 1) {
                        event.textChannel.deleteMessageById(messages.first().idLong)
                    } else {
                        event.textChannel.deleteMessages(messages)
                    }.queue()
                }

                it.editMessage("${event.textChannel.asMention} をクリーンアップしました。").deleteQueue(10, TimeUnit.SECONDS, bot.messageCacheManager)
            } catch (e: Exception) {
                it.editMessage("${event.textChannel.asMention} のクリーンアップに失敗しました。").deleteQueue(10, TimeUnit.SECONDS, bot.messageCacheManager)
            }
        }
    }
}

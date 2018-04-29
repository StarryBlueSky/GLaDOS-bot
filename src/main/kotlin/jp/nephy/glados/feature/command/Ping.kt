package jp.nephy.glados.feature.command

import com.jagrosh.jdautilities.command.CommandEvent
import jp.nephy.glados.feature.CommandFeature
import java.time.temporal.ChronoUnit


class Ping: CommandFeature() {
    init {
        name = "ping"
        help = "GLaDOSの遅延をチェックします。"
        guildOnly = false
    }

    override fun executeCommand(event: CommandEvent) {
        event.reply("Ping...") {
            val ping = event.message.creationTime.until(it.creationTime, ChronoUnit.MILLIS)
            it.editMessage("Ping: " + ping + "ms | WebSocket: " + event.jda.ping + "ms").queue()
        }
    }
}

package jp.nephy.glados.feature.command

import com.jagrosh.jdautilities.command.CommandEvent
import jp.nephy.glados.feature.CommandFeature


class Stop: CommandFeature() {
    init {
        name = "stop"
        help = "安全にシャットダウンします。"

        isAdminCommand = true
    }

    override fun executeCommand(event: CommandEvent) {
        event.reply("終了します...") {
            event.jda.shutdown()
        }
    }
}

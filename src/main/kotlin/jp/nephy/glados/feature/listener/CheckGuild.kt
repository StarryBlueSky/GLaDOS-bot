package jp.nephy.glados.feature.listener

import jp.nephy.glados.GLaDOS
import jp.nephy.glados.feature.ListenerFeature
import net.dv8tion.jda.core.events.ReadyEvent

class CheckGuild(bot: GLaDOS): ListenerFeature(bot) {
    override fun onReady(event: ReadyEvent) {
        if (event.jda.guilds.isEmpty()) {
            bot.logger.error { "このBotはどのサーバにも所属していません. 終了します." }
            event.jda.shutdown()
        }
    }
}

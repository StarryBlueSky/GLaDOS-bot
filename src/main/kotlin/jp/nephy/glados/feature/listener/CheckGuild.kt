package jp.nephy.glados.feature.listener

import jp.nephy.glados.feature.ListenerFeature
import jp.nephy.glados.logger
import net.dv8tion.jda.core.events.ReadyEvent

class CheckGuild: ListenerFeature() {
    override fun onReady(event: ReadyEvent) {
        if (event.jda.guilds.isEmpty()) {
            logger.error { "このBotはどのサーバにも所属していません. 終了します." }
            event.jda.shutdown()
        }
    }
}

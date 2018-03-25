package jp.nephy.glados.feature.listener

import jp.nephy.glados.GLaDOS
import jp.nephy.glados.feature.ListenerFeature
import net.dv8tion.jda.core.events.ReadyEvent

class CheckGuild(bot: GLaDOS): ListenerFeature(bot) {
    override fun onReady(event: ReadyEvent) {
        bot.logger.info { "サーバにGLaDOSを招待するリンクは https://discordapp.com/api/oauth2/authorize?client_id=${bot.config.clientId}&permissions=0&scope=bot です." }

        if (event.jda.guilds.isEmpty()) {
            bot.logger.error { "このBotはどのサーバにも所属していません. 終了します." }
            event.jda.shutdown()
        }
    }
}

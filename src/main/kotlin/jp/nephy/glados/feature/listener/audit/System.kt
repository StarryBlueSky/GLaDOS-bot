package jp.nephy.glados.feature.listener.audit

import jp.nephy.glados.GLaDOS
import jp.nephy.glados.feature.ListenerFeature
import net.dv8tion.jda.core.events.*


class System(bot: GLaDOS): ListenerFeature(bot) {
    override fun onReady(event: ReadyEvent) {
        bot.logger.info { "Discordへの接続が確立されました." }
        helper.slackLog(event) { "Discordへの接続が確立されました." }
    }

    override fun onDisconnect(event: DisconnectEvent) {
        bot.logger.warn { "Discordとの接続が切断されました." }
        helper.slackLog(event) { "Discordとの接続が切断されました." }
    }
    override fun onResume(event: ResumedEvent) {
        bot.logger.info { "Discordとの接続が再開しました." }
        helper.slackLog(event) { "Discordとの接続が再開しました." }
    }
    override fun onShutdown(event: ShutdownEvent) {
        bot.logger.info { "Discordとの接続が終了しました." }
        helper.slackLog(event) { "Discordとの接続が終了しました." }
    }

    override fun onException(event: ExceptionEvent) {
        bot.logger.error(event.cause) { "Discord接続中に例外が発生しました." }
        helper.slackLog(event) { "Discord接続中に例外が発生しました." }
    }
}

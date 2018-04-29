package jp.nephy.glados.feature.listener.audit

import jp.nephy.glados.feature.ListenerFeature
import jp.nephy.glados.logger
import net.dv8tion.jda.core.events.*


class System: ListenerFeature() {
    override fun onReady(event: ReadyEvent) {
        logger.info { "Discordへの接続が確立されました." }
        helper.slackLog(event) { "Discordへの接続が確立されました." }
    }

    override fun onDisconnect(event: DisconnectEvent) {
        logger.warn { "Discordとの接続が切断されました." }
        helper.slackLog(event) { "Discordとの接続が切断されました." }
    }

    override fun onResume(event: ResumedEvent) {
        logger.info { "Discordとの接続が再開しました." }
        helper.slackLog(event) { "Discordとの接続が再開しました." }
    }

    override fun onShutdown(event: ShutdownEvent) {
        logger.info { "Discordとの接続が終了しました." }
        helper.slackLog(event) { "Discordとの接続が終了しました." }
    }

    override fun onException(event: ExceptionEvent) {
        logger.error(event.cause) { "Discord接続中に例外が発生しました." }
        helper.slackLog(event) { "Discord接続中に例外が発生しました." }
    }
}

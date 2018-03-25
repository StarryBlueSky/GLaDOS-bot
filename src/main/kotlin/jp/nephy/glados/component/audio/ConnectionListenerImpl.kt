package jp.nephy.glados.component.audio

import jp.nephy.glados.GLaDOS
import jp.nephy.glados.component.helper.displayName
import net.dv8tion.jda.core.audio.hooks.ConnectionListener
import net.dv8tion.jda.core.audio.hooks.ConnectionStatus
import net.dv8tion.jda.core.entities.User

class ConnectionListenerImpl(private val bot: GLaDOS): ConnectionListener {
    override fun onStatusChange(status: ConnectionStatus) {
        bot.logger.info { "接続ステータスが変更されました: ${status.name}" }
    }

    override fun onUserSpeaking(user: User, speaking: Boolean) {
        if (speaking) {
            bot.logger.debug { "${user.displayName} がオーディオを送信しています." }
        } else {
            bot.logger.debug { "${user.displayName} がオーディオを送信を終了しました." }
        }
    }

    private var lastPing: Long = 0
    override fun onPing(ping: Long) {
        if (ping - lastPing > 50 || ping > 100) {
            bot.logger.debug { "VC: Pingが悪化しています. 現在の値: Ping ${ping}ms" }
        }
        lastPing = ping
    }
}

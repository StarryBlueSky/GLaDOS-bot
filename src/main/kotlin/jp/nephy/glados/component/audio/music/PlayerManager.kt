package jp.nephy.glados.component.audio.music

import jp.nephy.glados.GLaDOS
import net.dv8tion.jda.core.entities.Guild


class PlayerManager(private val bot: GLaDOS) {
    private val players = mutableMapOf<Long, GuildPlayer>()

    fun getGuildPlayer(guild: Guild): GuildPlayer {
        return synchronized(players) {
            players.getOrPut(guild.idLong) {
                GuildPlayer(bot, guild).apply {
                    guild.audioManager.isAutoReconnect = true
                    guild.audioManager.connectionListener = connectionListener
                    guild.audioManager.sendingHandler = sendingHandler
                    guild.audioManager.setReceivingHandler(receivingHandler)
                    bot.logger.info { "${guild.name}のプレイヤーが生成されました." }
                }
            }
        }
    }
}

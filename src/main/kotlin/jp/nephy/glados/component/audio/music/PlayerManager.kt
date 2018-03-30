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
                    guild.audioManager.sendingHandler = SilenceAudioSendHandler {
                        // バグ対応: https://github.com/DV8FromTheWorld/JDA/issues/653
                        guild.audioManager.sendingHandler = sendingHandler
                        guild.audioManager.setReceivingHandler(receivingHandler)
                        bot.logger.info { "[${voiceChannel.name} (${guild.name})] 無音の送信が終了しました." }
                    }

                    bot.logger.info { "[${voiceChannel.name} (${guild.name})] プレイヤーが生成されました." }
                }
            }
        }
    }
}

package jp.nephy.glados.feature.listener.music

import jp.nephy.glados.GLaDOS
import jp.nephy.glados.component.helper.isNoOneExceptSelf
import jp.nephy.glados.feature.ListenerFeature
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent

class PlayerPlayState: ListenerFeature() {
    override fun onGuildVoiceJoin(event: GuildVoiceJoinEvent) {
        val config = bot.config.getGuildConfig(event.guild)
        if (config.voiceChannel.general == null) {
            return
        }

        val guildPlayer = bot.playerManager.getGuildPlayer(event.guild)
        if (! guildPlayer.controls.isPlaying && ! event.channelJoined.isNoOneExceptSelf) {
            guildPlayer.controls.resume()
        }
    }

    override fun onGuildVoiceLeave(event: GuildVoiceLeaveEvent) {
        val config = bot.config.getGuildConfig(event.guild)
        if (config.voiceChannel.general == null) {
            return
        }

        val guildPlayer = bot.playerManager.getGuildPlayer(event.guild)
        if (guildPlayer.controls.isPlaying && event.channelLeft.isNoOneExceptSelf) {
            guildPlayer.controls.pause()
        }
    }
}

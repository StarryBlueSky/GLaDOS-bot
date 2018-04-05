package jp.nephy.glados.feature.listener.kaigen

import jp.nephy.glados.GLaDOS
import jp.nephy.glados.component.helper.Color
import jp.nephy.glados.component.helper.deleteQueue
import jp.nephy.glados.component.helper.embedMention
import jp.nephy.glados.feature.ListenerFeature
import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.Member
import net.dv8tion.jda.core.entities.VoiceChannel
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceSelfMuteEvent
import java.util.*
import java.util.concurrent.TimeUnit

class NoMuteChannel(bot: GLaDOS): ListenerFeature(bot) {
    private val cooldowns = mutableMapOf<Long, Long>()

    override fun onGuildVoiceJoin(event: GuildVoiceJoinEvent) {
        if (event.member.voiceState.isMuted) {
            move(event.member, event.channelJoined, event.guild)
        }
    }

    override fun onGuildVoiceMove(event: GuildVoiceMoveEvent) {
        if (event.member.voiceState.isMuted) {
            move(event.member, event.channelJoined, event.guild)
        }
    }

    override fun onGuildVoiceSelfMute(event: GuildVoiceSelfMuteEvent) {
        if (event.isSelfMuted) {
            move(event.member, event.voiceState.channel, event.guild)
        }
    }

    private fun move(member: Member, voiceChannel: VoiceChannel, guild: Guild) {
        val config = bot.config.getGuildConfig(guild)
        if (config.voiceChannel.noMute == null || guild.afkChannel == null) {
            return
        }
        if (voiceChannel.idLong != config.voiceChannel.noMute) {
            return
        }

        if (config.textChannel.bot != null) {
            if (member.checkCooldown()) {
                bot.jda.getTextChannelById(config.textChannel.bot).embedMention(member) {
                    title("⚠️ チャンネル規制")
                    descriptionBuilder {
                        appendln("ボイスチャンネル `${voiceChannel.name}` ではミュートが禁止されています。")
                        append("ご迷惑をおかけしますが AFKチャンネルに移動しました。")
                    }
                    color(Color.Bad)
                    timestamp()
                }.deleteQueue(60, TimeUnit.SECONDS)
            }
        }

        guild.controller.moveVoiceMember(member, guild.afkChannel).queue()
        member.updateCooldown()
    }

    private fun Member.updateCooldown() {
        cooldowns[user.idLong] = Date().time
    }
    private fun Member.checkCooldown(): Boolean {
        val time = cooldowns[user.idLong] ?: return true
        return Date().time - time > 1000 * 10
    }
}

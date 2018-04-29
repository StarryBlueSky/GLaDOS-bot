package jp.nephy.glados.feature.listener.kaigen

import jp.nephy.glados.GLaDOS
import jp.nephy.glados.feature.ListenerFeature
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.Member
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleRemoveEvent
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceSelfMuteEvent


class Inkya: ListenerFeature() {
    private fun addRole(guild: Guild, member: Member, jda: JDA) {
        val config = bot.config.getGuildConfig(guild)
        if (config.role.inkya == null) {
            return
        }

        if (member.roles.find { it.idLong == config.role.inkya } == null) {
            guild.controller.addSingleRoleToMember(member, jda.getRoleById(config.role.inkya)).queue()
        }
    }

    private fun removeRole(guild: Guild, member: Member, jda: JDA) {
        val config = bot.config.getGuildConfig(guild)
        if (config.role.inkya == null) {
            return
        }

        if (member.roles.find { it.idLong == config.role.inkya } != null) {
            guild.controller.removeSingleRoleFromMember(member, jda.getRoleById(config.role.inkya)).queue()
        }
    }

    // 条件を満たしていないのにロールが外れた場合のチェック
    override fun onGuildMemberRoleRemove(event: GuildMemberRoleRemoveEvent) {
        val config = bot.config.getGuildConfig(event.guild)
        if (config.role.inkya == null) {
            return
        }

        event.roles.filter { it.idLong == config.role.inkya }.forEach {
            if (event.member.voiceState.inVoiceChannel() && event.member.voiceState.channel != event.guild.afkChannel && event.member.voiceState.isSelfMuted) {
                addRole(event.guild, event.member, event.jda)
            }
        }
    }

    // VCから切断した場合
    override fun onGuildVoiceLeave(event: GuildVoiceLeaveEvent) {
        removeRole(event.guild, event.member, event.jda)
    }

    override fun onGuildVoiceMove(event: GuildVoiceMoveEvent) {
        // AFKチャンネルに移動した場合
        if (event.channelJoined == event.guild.afkChannel) {
            removeRole(event.guild, event.member, event.jda)
        } else if (event.channelLeft == event.guild.afkChannel && event.member.voiceState.isSelfMuted) {
            // マイクミュート状態でAFKチャンネルから移動した場合
            addRole(event.guild, event.member, event.jda)
        }
    }

    override fun onGuildVoiceSelfMute(event: GuildVoiceSelfMuteEvent) {
        // マイクミュートを解除した場合
        if (! event.isSelfMuted) {
            removeRole(event.guild, event.member, event.jda)
        } else if (event.isSelfMuted && event.member.voiceState.channel != event.guild.afkChannel) {
            // マイクミュートした場合 (AFKチャンネル以外)
            addRole(event.guild, event.member, event.jda)
        }
    }

    // マイクミュート状態でVCに参加した場合
    override fun onGuildVoiceJoin(event: GuildVoiceJoinEvent) {
        if (event.member.voiceState.isSelfMuted) {
            addRole(event.guild, event.member, event.jda)
        }
    }
}

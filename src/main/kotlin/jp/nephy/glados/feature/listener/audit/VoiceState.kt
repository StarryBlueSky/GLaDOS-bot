package jp.nephy.glados.feature.listener.audit

import jp.nephy.glados.feature.ListenerFeature
import net.dv8tion.jda.core.events.guild.voice.*


class VoiceState: ListenerFeature() {
    override fun onGuildVoiceJoin(event: GuildVoiceJoinEvent) {
        helper.slackLog(event) { "VC \"${event.channelJoined.name}\" に参加しました. このチャンネルには 現在${event.channelJoined.members.size}人が接続しています." }
    }

    override fun onGuildVoiceLeave(event: GuildVoiceLeaveEvent) {
        helper.slackLog(event) { "VC \"${event.channelLeft.name}\" から退出しました. このチャンネルには 現在${event.channelLeft.members.size}人が接続しています." }
    }

    override fun onGuildVoiceMove(event: GuildVoiceMoveEvent) {
        if (event.channelJoined == event.guild.afkChannel) {
            helper.slackLog(event) { "VC \"${event.channelLeft.name}\" から AFKチャンネル \"${event.channelJoined.name}\" に移動しました." }
        } else {
            helper.slackLog(event) { "VC \"${event.channelLeft.name}\" から \"${event.channelJoined.name}\" に移動しました." }
        }
    }

    override fun onGuildVoiceGuildMute(event: GuildVoiceGuildMuteEvent) {
        if (event.isGuildMuted) {
            helper.slackLog(event) { "サーバマイクミュートされました." }
        } else {
            helper.slackLog(event) { "サーバマイクミュート解除されました." }
        }
    }

    override fun onGuildVoiceSelfMute(event: GuildVoiceSelfMuteEvent) {
        if (event.isSelfMuted) {
            helper.slackLog(event) { "マイクミュートしました." }
        } else {
            helper.slackLog(event) { "マイクミュート解除しました." }
        }
    }

    override fun onGuildVoiceGuildDeafen(event: GuildVoiceGuildDeafenEvent) {
        if (event.isGuildDeafened) {
            helper.slackLog(event) { "サーバスピーカーミュートされました." }
        } else {
            helper.slackLog(event) { "サーバスピーカーミュート解除されました." }
        }
    }

    override fun onGuildVoiceSelfDeafen(event: GuildVoiceSelfDeafenEvent) {
        if (event.isSelfDeafened) {
            helper.slackLog(event) { "スピーカーミュートしました." }
        } else {
            helper.slackLog(event) { "スピーカーミュート解除しました." }
        }
    }
}

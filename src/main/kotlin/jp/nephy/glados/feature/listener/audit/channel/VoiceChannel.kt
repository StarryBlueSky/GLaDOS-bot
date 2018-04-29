package jp.nephy.glados.feature.listener.audit.channel

import jp.nephy.glados.GLaDOS
import jp.nephy.glados.component.helper.Color
import jp.nephy.glados.feature.ListenerFeature
import net.dv8tion.jda.core.events.channel.voice.VoiceChannelCreateEvent
import net.dv8tion.jda.core.events.channel.voice.VoiceChannelDeleteEvent
import net.dv8tion.jda.core.events.channel.voice.update.*


class VoiceChannel(bot: GLaDOS): ListenerFeature(bot) {
    override fun onVoiceChannelCreate(event: VoiceChannelCreateEvent) {
        helper.messageLog(event, "ボイスチャンネル作成", Color.Good) { "`${event.channel.name}` が作成されました。" }
    }
    override fun onVoiceChannelDelete(event: VoiceChannelDeleteEvent) {
        helper.messageLog(event, "ボイスチャンネル削除", Color.Bad) { "`${event.channel.name}` が削除されました。" }
    }
    override fun onVoiceChannelUpdateName(event: VoiceChannelUpdateNameEvent) {
        helper.messageLog(event, "ボイスチャンネル名前変更", Color.Neutral) { "`${event.oldName}` から `${event.channel.name}` に名前が変更されました。" }
    }
    override fun onVoiceChannelUpdateParent(event: VoiceChannelUpdateParentEvent) {
        helper.messageLog(event, "ボイスチャンネル所属カテゴリー変更", Color.Neutral) { "`${event.channel.name}` は カテゴリー `${event.oldParent?.name}` から `${event.channel.parent?.name}` に移動しました。" }
    }

    override fun onVoiceChannelUpdateBitrate(event: VoiceChannelUpdateBitrateEvent) {
        helper.slackLog(event) { "ボイスチャンネル `${event.channel.name}` のビットレートが `${event.oldBitrate}kbps` から `${event.channel.bitrate}kbps` に変更されました. (${event.guild.name})" }
    }
    override fun onVoiceChannelUpdatePermissions(event: VoiceChannelUpdatePermissionsEvent) {
        helper.slackLog(event) { "ボイスチャンネル `${event.channel.name}` の権限が変更されました. (${event.guild.name})" }
    }
    override fun onVoiceChannelUpdateUserLimit(event: VoiceChannelUpdateUserLimitEvent) {
        helper.slackLog(event) { "ボイスチャンネル `${event.channel.name}` の人数制限が `${event.oldUserLimit}` から `${event.channel.userLimit}` に変更されました. (${event.guild.name})" }
    }
    override fun onVoiceChannelUpdatePosition(event: VoiceChannelUpdatePositionEvent) {
        helper.slackLog(event) { "ボイスチャンネル `${event.channel.name}` の順序が変更されました. (${event.guild.name})" }
    }
}

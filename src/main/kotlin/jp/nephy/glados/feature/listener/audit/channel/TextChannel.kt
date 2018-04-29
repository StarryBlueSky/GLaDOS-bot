package jp.nephy.glados.feature.listener.audit.channel

import jp.nephy.glados.GLaDOS
import jp.nephy.glados.component.helper.Color
import jp.nephy.glados.feature.ListenerFeature
import net.dv8tion.jda.core.events.channel.text.TextChannelCreateEvent
import net.dv8tion.jda.core.events.channel.text.TextChannelDeleteEvent
import net.dv8tion.jda.core.events.channel.text.update.*


class TextChannel(bot: GLaDOS): ListenerFeature(bot) {
    override fun onTextChannelCreate(event: TextChannelCreateEvent) {
        helper.messageLog(event, "テキストチャンネル作成", Color.Good) { "${event.channel.asMention} が作成されました。" }
    }
    override fun onTextChannelDelete(event: TextChannelDeleteEvent) {
        helper.messageLog(event, "テキストチャンネル削除", Color.Bad) { "`${event.channel.name}` が削除されました。" }
    }
    override fun onTextChannelUpdateName(event: TextChannelUpdateNameEvent) {
        helper.messageLog(event, "テキストチャンネル名前変更", Color.Neutral) { "`${event.oldName}` から ${event.channel.asMention} に名前が変更されました。" }
    }
    override fun onTextChannelUpdateNSFW(event: TextChannelUpdateNSFWEvent) {
        if (event.channel.isNSFW) {
            helper.messageLog(event, "テキストチャンネルNSFW有効化", Color.Bad) { "${event.channel.asMention} はNSFW指定になりました。" }
        } else {
            helper.messageLog(event, "テキストチャンネルNSFW無効化", Color.Good) { "${event.channel.asMention} のNSFW指定は解除されました。" }
        }
    }
    override fun onTextChannelUpdateParent(event: TextChannelUpdateParentEvent) {
        helper.messageLog(event, "テキストチャンネル所属カテゴリー変更", Color.Neutral) { "${event.channel.asMention} は カテゴリー `${event.oldParent?.name}` から `${event.channel.parent?.name}` に移動しました。" }
    }

    override fun onTextChannelUpdateTopic(event: TextChannelUpdateTopicEvent) {
        if (event.oldTopic.isNullOrEmpty() && event.channel.topic.isNullOrEmpty()) {
            return
        }

        helper.slackLog(event) { "テキストチャンネル `${event.channel.name}` のトピックが `${event.oldTopic}` から `${event.channel.topic}` に変更されました. (${event.guild.name})" }
    }
    override fun onTextChannelUpdatePermissions(event: TextChannelUpdatePermissionsEvent) {
        helper.slackLog(event) { "テキストチャンネル `${event.channel.name}` の権限が変更されました. (${event.guild.name})" }
    }

    override fun onTextChannelUpdatePosition(event: TextChannelUpdatePositionEvent) {
        helper.slackLog(event) { "テキストチャンネル `${event.channel.name}` の順序が変更されました. (${event.guild.name})" }
    }
}

package jp.nephy.glados.feature.listener.audit

import jp.nephy.glados.GLaDOS
import jp.nephy.glados.component.helper.Color
import jp.nephy.glados.feature.ListenerFeature
import net.dv8tion.jda.core.events.emote.EmoteAddedEvent
import net.dv8tion.jda.core.events.emote.EmoteRemovedEvent
import net.dv8tion.jda.core.events.emote.update.EmoteUpdateNameEvent


class Emoji(bot: GLaDOS): ListenerFeature(bot) {
    override fun onEmoteAdded(event: EmoteAddedEvent) {
        helper.messageLog(event, "絵文字追加", Color.Good) { "絵文字 ${event.emote.asMention} `:${event.emote.name}:` が追加されました。" }
    }
    override fun onEmoteRemoved(event: EmoteRemovedEvent) {
        helper.messageLog(event, "絵文字削除", Color.Bad) { "絵文字 `:${event.emote.name}:` が削除されました。" }
    }
    override fun onEmoteUpdateName(event: EmoteUpdateNameEvent) {
        helper.messageLog(event, "絵文字名前変更", Color.Neutral) { "絵文字の名前が :${event.newName}: `:${event.oldName}:` → `:${event.newName}:` に変更されました。" }
    }
}

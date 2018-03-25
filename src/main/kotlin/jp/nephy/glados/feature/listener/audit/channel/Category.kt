package jp.nephy.glados.feature.listener.audit.channel

import jp.nephy.glados.GLaDOS
import jp.nephy.glados.component.helper.Color
import jp.nephy.glados.feature.ListenerFeature
import net.dv8tion.jda.core.events.channel.category.CategoryCreateEvent
import net.dv8tion.jda.core.events.channel.category.CategoryDeleteEvent
import net.dv8tion.jda.core.events.channel.category.update.CategoryUpdateNameEvent
import net.dv8tion.jda.core.events.channel.category.update.CategoryUpdatePermissionsEvent
import net.dv8tion.jda.core.events.channel.category.update.CategoryUpdatePositionEvent


class Category(bot: GLaDOS): ListenerFeature(bot) {
    override fun onCategoryCreate(event: CategoryCreateEvent) {
        helper.messageLog(event, "カテゴリー作成", Color.Good) { "`${event.category.name}` が作成されました。" }
    }
    override fun onCategoryDelete(event: CategoryDeleteEvent) {
        helper.messageLog(event, "カテゴリー削除", Color.Bad) { "`${event.category.name}` が削除されました。" }
    }
    override fun onCategoryUpdateName(event: CategoryUpdateNameEvent) {
        helper.messageLog(event, "カテゴリー名前変更", Color.Neutral) { "`${event.oldName}` から `${event.category.name}` に名前が変更されました。" }
    }

    override fun onCategoryUpdatePermissions(event: CategoryUpdatePermissionsEvent) {
        bot.logger.info { "カテゴリー `${event.category.name}` の権限が変更されました. (${event.guild.name})" }
    }
    override fun onCategoryUpdatePosition(event: CategoryUpdatePositionEvent) {
        bot.logger.info { "カテゴリー `${event.category.name}` の順序が変更されました. (${event.guild.name})" }
    }
}

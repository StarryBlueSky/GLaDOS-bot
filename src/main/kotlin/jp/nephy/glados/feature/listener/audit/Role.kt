package jp.nephy.glados.feature.listener.audit

import jp.nephy.glados.GLaDOS
import jp.nephy.glados.component.helper.Color
import jp.nephy.glados.feature.ListenerFeature
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleAddEvent
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleRemoveEvent
import net.dv8tion.jda.core.events.role.RoleDeleteEvent
import net.dv8tion.jda.core.events.role.update.*


class Role(bot: GLaDOS): ListenerFeature(bot) {
    override fun onRoleDelete(event: RoleDeleteEvent) {
        helper.messageLog(event, "ロール削除", Color.Bad) { "ロール `${event.role.name}` が削除されました。" }
    }
    override fun onRoleUpdateName(event: RoleUpdateNameEvent) {
        if (event.oldName == "new role") {
            helper.messageLog(event, "ロール追加", Color.Good) { "ロール `${event.role.name}` が作成されました。" }
        } else {
            helper.messageLog(event, "ロール名前変更", Color.Neutral) { "ロールの名前が `${event.oldName}` から `${event.role.name}` に変更されました。" }
        }
    }

    override fun onGuildMemberRoleAdd(event: GuildMemberRoleAddEvent) {
        event.roles.forEach {
            helper.slackLog(event) { "ロール \"${it.name}\" が付与されました." }
        }
    }
    override fun onGuildMemberRoleRemove(event: GuildMemberRoleRemoveEvent) {
        event.roles.forEach {
            helper.slackLog(event) { "ロール \"${it.name}\" が剥奪されました." }
        }
    }

    override fun onRoleUpdateColor(event: RoleUpdateColorEvent) {
        helper.slackLog(event) { "ロール `${event.role.name}` の色が ${event.oldColor} から ${event.role.color} に変更されました. (${event.guild.name})" }
    }
    override fun onRoleUpdateMentionable(event: RoleUpdateMentionableEvent) {
        if (event.wasMentionable()) {
            helper.slackLog(event) { "ロール `${event.role.name}` がメンション不可能になりました. (${event.guild.name})" }
        } else {
            helper.slackLog(event) { "ロール `${event.role.name}` がメンション可能になりました. (${event.guild.name})" }
        }
    }
    override fun onRoleUpdateHoisted(event: RoleUpdateHoistedEvent) {
        if (event.wasHoisted()) {
            helper.slackLog(event) { "ロール `${event.role.name}` がオンラインユーザに表示されなくなりました. (${event.guild.name})" }
        } else {
            helper.slackLog(event) { "ロール `${event.role.name}` がオンラインユーザに表示されるようになりました. (${event.guild.name})" }
        }
    }
    override fun onRoleUpdatePermissions(event: RoleUpdatePermissionsEvent) {
        helper.slackLog(event) { "ロール `${event.role.name}` の権限が変更されました. (${event.guild.name})" }
    }
    override fun onRoleUpdatePosition(event: RoleUpdatePositionEvent) {
        helper.slackLog(event) { "ロール `${event.role.name}` の順序が変更されました. (${event.guild.name})" }
    }
}

package jp.nephy.glados.feature.listener.audit

import jp.nephy.glados.GLaDOS
import jp.nephy.glados.component.helper.Color
import jp.nephy.glados.feature.ListenerFeature
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent
import net.dv8tion.jda.core.events.guild.member.GuildMemberNickChangeEvent


class Member(bot: GLaDOS): ListenerFeature(bot) {
    override fun onGuildMemberNickChange(event: GuildMemberNickChangeEvent) {
        helper.slackLog(event) { "名前を ${event.prevNick ?: event.user.name} から ${event.newNick ?: event.user.name} に変更しました." }
    }

    override fun onGuildMemberJoin(event: GuildMemberJoinEvent) {
        helper.messageLog(event, "メンバー追加", Color.Good) { "${event.member.asMention} がサーバに参加しました。" }
    }
    override fun onGuildMemberLeave(event: GuildMemberLeaveEvent) {
        helper.messageLog(event, "メンバー退出", Color.Bad) { "${event.member.asMention} がサーバから退出しました。" }
    }
}

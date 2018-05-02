package jp.nephy.glados.feature.listener.kaigen

import jp.nephy.glados.component.helper.embedMessage
import jp.nephy.glados.feature.ListenerFeature
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent


class Greeting: ListenerFeature() {
    override fun onGuildMemberJoin(event: GuildMemberJoinEvent) {
        val config = bot.config.getGuildConfig(event.guild)
        if (config.textChannel.general == null || config.textChannel.rules == null) {
            return
        }

        val rulesChannel = event.jda.getTextChannelById(config.textChannel.rules)
        event.jda.getTextChannelById(config.textChannel.general).embedMessage {
            title("${event.guild.name} にようこそ！")
            description { "${event.member.asMention} さんようこそ。\n参加に際しては ${rulesChannel.asMention} の内容を確認してください。" }
        }.queue()
    }
}

package jp.nephy.glados.component.api.twitter.kashiwa

import jp.nephy.glados.GLaDOS
import jp.nephy.glados.component.config.GuildConfig
import jp.nephy.glados.component.helper.StringLinkedSingleCache
import jp.nephy.glados.component.helper.embedMessage
import jp.nephy.penicillin.model.*
import jp.nephy.penicillin.request.streaming.UserStream
import net.dv8tion.jda.core.entities.TextChannel

class UserStreamListener(val bot: GLaDOS): UserStream.Listener {
    companion object {
        var hateCommandString by StringLinkedSingleCache{ "hate" }
    }

    private val kashiwaGuild: List<GuildConfig> = bot.config.guilds.filter { it.textChannel.iHateSuchKashiwa != null }
    private val kashiwaChannel: List<TextChannel> = kashiwaGuild.map { bot.jda.getTextChannelById(it.textChannel.iHateSuchKashiwa!!) }
    private val client = bot.apiClient.twitter
    private val id = client.account.verifyCredentials().complete().result.id

    override fun onStatus(status: Status) {
        if(status.inReplyToUserId == id) {
            if(Regex(hateCommandString).find(status.text) != null) {
                kashiwaChannel.forEach {
                    message(it, status)
                }
            }
        }
    }

    //TODO
    override fun onDelete(delete: StreamDelete) {}

    fun message(channel: TextChannel, status: Status) {
        val user = status.user
        val url = "https://twitter.com/" + user.screenName + "/" + status.idStr
        channel.embedMessage {
            title(user.name + " (@" + user.screenName + ")")
            description { if (user.protected) {status.fullText + "\n"} else "" + url }
        }.queue()
    }
}

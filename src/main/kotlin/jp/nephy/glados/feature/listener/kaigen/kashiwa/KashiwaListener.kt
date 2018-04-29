package jp.nephy.glados.feature.listener.kaigen.kashiwa

import jp.nephy.glados.GLaDOS
import jp.nephy.glados.component.helper.StringLinkedSingleCache
import jp.nephy.penicillin.model.Status
import jp.nephy.penicillin.model.StreamDelete
import jp.nephy.penicillin.request.streaming.UserStreamListener


class KashiwaListener(private val bot: GLaDOS): UserStreamListener {
    companion object {
        var hateCommandString by StringLinkedSingleCache { "hate" }
        private val hateCommand: Regex
            get() = hateCommandString.toRegex()
    }

    private val kashiwaChannels = bot.config.guilds.filter { it.textChannel.iHateSuchKashiwa != null }.map { bot.jda.getTextChannelById(it.textChannel.iHateSuchKashiwa!!) }

    override fun onStatus(status: Status) {
        if (status.inReplyToUserId == 911815894384308224) {
            if (hateCommand.containsMatchIn(status.fullText)) {
                kashiwaChannels.forEach {
                    it.revealTweet(status.fullText, bot)
                }
            }
        }
    }

    // TODO
    override fun onDelete(delete: StreamDelete) {}
}

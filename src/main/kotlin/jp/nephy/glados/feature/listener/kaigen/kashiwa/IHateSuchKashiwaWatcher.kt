package jp.nephy.glados.feature.listener.kaigen.kashiwa

import jp.nephy.glados.GLaDOS
import jp.nephy.glados.component.helper.Color
import jp.nephy.glados.component.helper.embedMessage
import jp.nephy.glados.component.helper.isSelf
import jp.nephy.glados.feature.ListenerFeature
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.core.events.message.guild.GuildMessageUpdateEvent
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent


class IHateSuchKashiwaWatcher(bot: GLaDOS): ListenerFeature(bot) {
    private val twitterUrl = "(?:http(?:s)?://)?(?:m|mobile)?twitter\\.com/(\\w+?)/status/(\\d+)".toRegex()

    override fun onGuildMessageReactionAdd(event: GuildMessageReactionAddEvent) {
        val config = bot.config.getGuildConfig(event.guild)
        if (event.member == null || event.user.isSelf || event.channel.idLong != config.textChannel.iHateSuchKashiwa) {
            return
        }

        val message = bot.messageCacheManager.get(event.messageIdLong, remove = false) ?: return
        val match = twitterUrl.find(message.contentDisplay)
        if (match == null) {
            bot.logger.info { "Twitter URLパターンに一致しないメッセージです: ${message.contentDisplay}" }
            return
        }

        val emoji = HateEmoji.fromEmoji(event.reactionEmote.name)
        if (emoji == null) {
            bot.logger.info { "`${message.contentDisplay}`に未知のリアクション ${event.reactionEmote.name} が付きました." }
            return
        }

        // TODO
        if (! GLaDOS.debug) {
            return
        }

        val face1Count = message.reactions.count { it.reactionEmote.name == HateEmoji.Face1.emoji }
        val face2Count = message.reactions.count { it.reactionEmote.name == HateEmoji.Face2.emoji }
        val face3Count = message.reactions.count { it.reactionEmote.name == HateEmoji.Face3.emoji }
        val total = face1Count + face2Count + face3Count
        if (total > 5 || face1Count > 3 || face2Count > 3 || face3Count > 3) {
            event.channel.embedMessage {
                title("かいげん語録追加")
                author("@${match.groupValues[1]}", match.value)
                description {
                    "${match.value} が かいげん語録に追加されました！おめでとうございます！"
                }
                timestamp()
                color(Color.Good)
            }.queue {
                bot.logger.info { "かいげん語録追加: ${match.value}" }
            }
        }
    }

    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        val config = bot.config.getGuildConfig(event.guild)
        if (event.member == null || event.member.user.isSelf || event.channel.idLong != config.textChannel.iHateSuchKashiwa) {
            return
        }

        if (! twitterUrl.containsMatchIn(event.message.contentDisplay)) {
            bot.logger.info { "Twitter URLパターンに一致しないメッセージです: ${event.message.contentDisplay}" }
            return
        }

        val statusInfo = twitterUrl.matchEntire(event.message.contentDisplay)!!.destructured.toList()
        val (screenName, statusId) = statusInfo[0] to statusInfo[1]
        val twitter = bot.apiClient.twitter
        twitter.user.show(screenName).queue {
            val user = it.result
            twitter.status.show(statusId.toLong()).queue {
                val status = it.result
                if (user.protected) {
                    event.channel.embedMessage {
                        title(user.name + " (@" + user.screenName + ")")
                        description { status.fullText }
                    }
                }
            }
        }

        HateEmoji.values().forEach {
            event.message.addReaction(it.emoji).queue()
        }
    }

    override fun onGuildMessageUpdate(event: GuildMessageUpdateEvent) {
        val config = bot.config.getGuildConfig(event.guild)
        if (event.member == null || event.member.user.isSelf || event.channel.idLong != config.textChannel.iHateSuchKashiwa) {
            return
        }

        if (! twitterUrl.containsMatchIn(event.message.contentDisplay)) {
            bot.logger.info { "Twitter URLパターンに一致しないメッセージです: ${event.message.contentDisplay}" }
            return
        }

        HateEmoji.values().forEach { e ->
            if (event.message.reactions.any { it.reactionEmote.name == e.emoji }) {
                return@forEach
            }

            event.message.addReaction(e.emoji).queue()
        }
    }
}

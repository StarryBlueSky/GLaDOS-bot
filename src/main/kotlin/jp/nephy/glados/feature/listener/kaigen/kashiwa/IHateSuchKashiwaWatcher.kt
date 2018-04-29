package jp.nephy.glados.feature.listener.kaigen.kashiwa

import jp.nephy.glados.GLaDOS
import jp.nephy.glados.component.helper.Color
import jp.nephy.glados.component.helper.embedMessage
import jp.nephy.glados.component.helper.isSelf
import jp.nephy.glados.feature.ListenerFeature
import net.dv8tion.jda.core.entities.TextChannel
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.core.events.message.guild.GuildMessageUpdateEvent
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent

val twitterUrl = "(?:http(?:s)?://)?(?:m|mobile)?twitter\\.com/((?:\\w|_){1,16})/status/(\\d+)".toRegex()
fun TextChannel.revealTweet(text: String, bot: GLaDOS) {
    twitterUrl.findAll(text).forEach { matched ->
        val (screenName, statusId) = matched.destructured
        bot.apiClient.twitter.user.show(screenName = screenName).queue { user ->
            if (user.result.protected) {
                bot.apiClient.twitter.status.show(id = statusId.toLong()).queue { status ->
                    embedMessage {
                        author("${user.result.name} @${user.result.screenName}", "https://twitter.com/${user.result.screenName}", user.result.profileImageUrlHttps)
                        descriptionBuilder {
                            appendln(status.result.fullText)
                            appendln()
                            append(matched.value)
                        }
                        if (status.result.extendedEntities?.media.orEmpty().isNotEmpty()) {
                            image(status.result.extendedEntities!!.media.first().mediaUrlHttps)
                        }

                        color(Color.Twitter)
                        footer("Twitter", "https://abs.twimg.com/icons/apple-touch-icon-192x192.png")
                        timestamp()
                    }.queue()
                }
            }
        }
    }
}

class IHateSuchKashiwaWatcher(bot: GLaDOS): ListenerFeature(bot) {
    init {
        bot.apiClient.twitter.stream.userStream().listen(KashiwaListener(bot)).start(wait = false)
    }

    override fun onGuildMessageReactionAdd(event: GuildMessageReactionAddEvent) {
        val config = bot.config.getGuildConfig(event.guild)
        if (event.member == null || event.user.isSelf || event.channel.idLong != config.textChannel.iHateSuchKashiwa) {
            return
        }

        val message = bot.messageCacheManager.get(event.messageIdLong) ?: return
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

        event.channel.revealTweet(event.message.contentDisplay, bot)

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

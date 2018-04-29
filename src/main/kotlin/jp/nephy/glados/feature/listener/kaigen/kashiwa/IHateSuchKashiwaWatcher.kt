package jp.nephy.glados.feature.listener.kaigen.kashiwa

import jp.nephy.glados.GLaDOS
import jp.nephy.glados.component.helper.Color
import jp.nephy.glados.component.helper.embedMessage
import jp.nephy.glados.component.helper.isSelf
import jp.nephy.glados.feature.ListenerFeature
import jp.nephy.glados.logger
import net.dv8tion.jda.core.entities.TextChannel
import net.dv8tion.jda.core.events.ReadyEvent
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.core.events.message.guild.GuildMessageUpdateEvent
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent
import kotlin.concurrent.thread

val twitterUrl = "(?:http(?:s)?://)?(?:m|mobile)?twitter\\.com/((?:\\w|_){1,16})/status/(\\d+)".toRegex()
fun TextChannel.revealTweet(text: String, onlyWhenProtected: Boolean = false) {
    val twitter = GLaDOS.instance.apiClient.twitter
    twitterUrl.findAll(text).forEach { matched ->
        val statusId = matched.groupValues.last().toLong()
        twitter.status.show(id = statusId).queue { status ->
            if (! onlyWhenProtected || status.result.user.protected) {
                embedMessage {
                    author("${status.result.user.name} @${status.result.user.screenName}", "https://twitter.com/${status.result.user.screenName}", status.result.user.profileImageUrlHttps)
                    descriptionBuilder {
                        appendln(status.result.fullText)
                        appendln()
                        append(matched.value)
                    }
                    if (status.result.extendedEntities?.media.orEmpty().isNotEmpty()) {
                        image(status.result.extendedEntities !!.media.first().mediaUrlHttps)
                    }

                    color(Color.Twitter)
                    footer("Twitter", "https://abs.twimg.com/icons/apple-touch-icon-192x192.png")
                    timestamp()
                }.queue()
            }
        }
    }
}

class IHateSuchKashiwaWatcher: ListenerFeature() {
    override fun onReady(event: ReadyEvent?) {
        thread(name = "KashiwaStream Keeper") {
            val kashiwaChannels = GLaDOS.instance.config.guilds.filter { it.textChannel.iHateSuchKashiwa != null }.mapNotNull { GLaDOS.instance.jda.getTextChannelById(it.textChannel.iHateSuchKashiwa!!) }
            if (kashiwaChannels.isEmpty()) {
                return@thread
            }

            bot.apiClient.twitter.stream.userStream().listen(KashiwaListener(kashiwaChannels)).start(wait = true, autoReconnect = true)
        }
    }

    override fun onGuildMessageReactionAdd(event: GuildMessageReactionAddEvent) {
        val config = bot.config.getGuildConfig(event.guild)
        if (event.member == null || event.user.isSelf || event.channel.idLong != config.textChannel.iHateSuchKashiwa) {
            return
        }

        val message = bot.messageCacheManager.get(event.messageIdLong) ?: return
        val match = twitterUrl.find(message.contentDisplay)
        if (match == null) {
            logger.info { "Twitter URLパターンに一致しないメッセージです: ${message.contentDisplay}" }
            return
        }

        val emoji = HateEmoji.fromEmoji(event.reactionEmote.name)
        if (emoji == null) {
            logger.info { "`${message.contentDisplay}`に未知のリアクション ${event.reactionEmote.name} が付きました." }
            return
        }

        // TODO
        if (! bot.isDebugMode) {
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
                logger.info { "かいげん語録追加: ${match.value}" }
            }
        }
    }

    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        val config = bot.config.getGuildConfig(event.guild)
        if (event.member == null || event.member.user.isSelf || event.channel.idLong != config.textChannel.iHateSuchKashiwa) {
            return
        }

        if (! twitterUrl.containsMatchIn(event.message.contentDisplay)) {
            logger.info { "Twitter URLパターンに一致しないメッセージです: ${event.message.contentDisplay}" }
            return
        }

        event.channel.revealTweet(event.message.contentDisplay, onlyWhenProtected = true)

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
            logger.info { "Twitter URLパターンに一致しないメッセージです: ${event.message.contentDisplay}" }
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

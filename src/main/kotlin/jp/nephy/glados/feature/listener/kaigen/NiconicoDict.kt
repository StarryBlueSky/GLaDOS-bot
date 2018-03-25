package jp.nephy.glados.feature.listener.kaigen

import jp.nephy.glados.GLaDOS
import jp.nephy.glados.component.helper.Color
import jp.nephy.glados.component.helper.embedMention
import jp.nephy.glados.feature.ListenerFeature
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import org.jsoup.Jsoup
import java.net.URL


class NiconicoDict(bot: GLaDOS): ListenerFeature(bot) {
    private val pattern = "^(.+?)(?:#)?とは$".toRegex()
    private val transportPattern = "location.replace\\('(.+?)'\\)".toRegex()

    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        val config = bot.config.getGuildConfig(event.guild)
        if (! config.option.useNiconicoDict) {
            return
        }
        val match = pattern.matchEntire(event.message.contentDisplay) ?: return

        val word = match.groupValues[1]
        var articleUrl = "http://dic.nicovideo.jp/a/$word"
        var content = URL(articleUrl).readText()
        val transport = transportPattern.find(content)
        if (transport != null) {
            articleUrl = transport.groupValues[1]
            content = URL(articleUrl).readText()
        }

        val doc = Jsoup.parse(content)

        if (! doc.html().contains("まだ記事が書かれていません")) {
            val result = linkedMapOf<String, String>()
            var lastSection: String? = null
            var description: String? = ""

            doc.select("#article").first().children()
                    .filter { it.attr("id") != "page-menu" && !arrayOf("div", "ul", "table").contains(it.tagName()) }
                    .takeWhile { !arrayOf("関連動画", "関連項目", "記事編集").contains(it.text()) }
                    .takeWhile { !it.attr("class").startsWith("adsense-") }
                    .forEach {
                        when {
                            arrayOf("h1", "h2", "h3").contains(it.tagName()) -> {
                                lastSection = it.text()
                                result[lastSection!!] = ""
                            }
                            lastSection.isNullOrEmpty() -> {
                                description += it.text().trim()
                            }
                            else -> {
                                result[lastSection!!] = result.getOrDefault(lastSection!!, "") + it.text().trim()
                            }
                        }
                    }

            var text = "$description\n\n"
            result.filterNot { it.value.isBlank() }.forEach { k, v ->
                text += "$k\n    $v"
            }

            event.channel.embedMention(event.member) {
                title("$word とは")
                author("ニコニコ大百科", articleUrl, "http://dic.nicovideo.jp/img/nv.gif")
                description { "${text.take(500)}..." }
                timestamp()
                color(Color.Niconico)
            }
        } else {
            event.channel.embedMention(event.member) {
                title("$word とは")
                author("ニコニコ大百科", articleUrl, "http://dic.nicovideo.jp/img/nv.gif")
                description { "記事が見つかりませんでした。" }
                timestamp()
                color(Color.Niconico)
            }
        }.queue()
    }
}

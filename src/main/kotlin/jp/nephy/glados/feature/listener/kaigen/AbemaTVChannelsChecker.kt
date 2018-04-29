package jp.nephy.glados.feature.listener.kaigen

import jp.nephy.glados.GLaDOS
import jp.nephy.glados.component.helper.StringLinkedListCache
import jp.nephy.glados.feature.ListenerFeature
import jp.nephy.glados.logger
import jp.nephy.jsonkt.JsonKt
import jp.nephy.jsonkt.get
import jp.nephy.jsonkt.jsonArray
import jp.nephy.jsonkt.string
import net.dv8tion.jda.core.events.ReadyEvent
import net.dv8tion.jda.webhook.WebhookClient
import net.dv8tion.jda.webhook.WebhookClientBuilder
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread


class AbemaTVChannelsChecker: ListenerFeature() {
    private val headers = Headers.Builder()
            .add("Accept-Language", "ja")
            .add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36")
            .build()
    private val client = OkHttpClient()

    private var lastAbemaTVChannels by StringLinkedListCache { getAbemaTVChannels().map { "${it.first}+${it.second}" } }

    override fun onReady(event: ReadyEvent) {
        thread(name = "AbemaTV Channels Checker") {
            val clients = bot.config.guilds.mapNotNull { it.webhook.abemaTV }.map { WebhookClientBuilder(it).build() }
            while (true) {
                try {
                    check(clients)
                } catch (e: Exception) {
                    logger.error(e) { "AbemaTVチャンネルのチェック中に例外が発生しました." }
                }

                TimeUnit.MINUTES.sleep(1)
            }
        }
    }

    private fun getAbemaTVChannels(): List<Pair<String, String>> {
        val response = client.newCall(
                Request.Builder()
                        .url("https://api.abema.io/v1/channels")
                        .headers(headers)
                        .build()
        ).execute()
        val content = response.body()!!.string()

        return JsonKt.toJsonObject(content)["channels"].jsonArray.map {
            Pair(it["id"].string, it["name"].string)
        }
    }

    private fun check(webhooks: List<WebhookClient>) {
        val channels = getAbemaTVChannels()
        val oldChannels = lastAbemaTVChannels.map { it.split("+") }.map { Pair(it.first(), it.last()) }

        channels.forEach { new ->
            if (oldChannels.count { it.first == new.first } == 0) {
                webhooks.forEach {
                    it.send("${new.second} (https://abema.tv/now-on-air/${new.first}) が追加されました。")
                }
            } else {
                val oldName = oldChannels.find { it.first == new.first }!!.second
                if (new.second != oldName) {
                    webhooks.forEach {
                        it.send("$oldName は ${new.second} (https://abema.tv/now-on-air/${new.first}) に改名されました。")
                    }
                }
            }
        }
        oldChannels.forEach { old ->
            if (channels.count { it.first == old.first } != 0) {
                return@forEach
            }

            webhooks.forEach {
                it.send("${old.second} (https://abema.tv/now-on-air/${old.first}) が削除されました。")
            }
        }
        lastAbemaTVChannels = channels.map { "${it.first}+${it.second}" }
    }
}

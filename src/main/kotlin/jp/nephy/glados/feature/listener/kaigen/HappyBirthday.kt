package jp.nephy.glados.feature.listener.kaigen

import jp.nephy.glados.component.config.GuildConfig
import jp.nephy.glados.component.helper.Color
import jp.nephy.glados.component.helper.StringLinkedSingleCache
import jp.nephy.glados.component.helper.embedMessage
import jp.nephy.glados.feature.ListenerFeature
import jp.nephy.glados.logger
import jp.nephy.glados.model.Member
import jp.nephy.jsonkt.JsonKt
import jp.nephy.jsonkt.JsonModel
import jp.nephy.jsonkt.jsonObject
import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.events.ReadyEvent
import java.nio.file.Path
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread


class HappyBirthday: ListenerFeature() {
    private var lastDate by StringLinkedSingleCache { "" }

    override fun onReady(event: ReadyEvent) {
        for (guild in event.jda.guilds) {
            val config = bot.config.getGuildConfig(guild)
            if (! config.option.useHappyBirthday || config.textChannel.general == null) {
                continue
            }
            watch(guild, config)
        }
    }

    private inline fun <reified T: JsonModel> parseAsList(path: Path): List<T> {
        return JsonKt.toJsonArray(path).map { JsonKt.parse<T>(it.jsonObject) }
    }

    private val members: List<Member>
        get() = parseAsList(Paths.get("C:/Server/Authenticator/members.json"))
    private val timezone = TimeZone.getTimeZone("Asia/Tokyo")
    private val date: String
        get() = SimpleDateFormat("MM/dd").apply {
            timeZone = timezone
        }.format(Date())

    private fun watch(guild: Guild, config: GuildConfig) {
        thread(name = "Happy Birthday Watcher") {
            while (true) {
                try {
                    if (lastDate != date) {
                        val calendar = Calendar.getInstance(timezone)
                        val month = calendar.get(Calendar.MONTH) + 1
                        val day = calendar.get(Calendar.DATE)

                        members.filter { it.profile.birthday?.month == month && it.profile.birthday?.day == day }.forEach {
                            val discord = it.accounts.discord.firstOrNull { ! it.private }
                            val discordMember = guild.members.find { discord?.tag == "${it.user.name}#${it.user.discriminator}" }
                            val twitter = it.accounts.twitter.firstOrNull { ! it.private }

                            guild.getTextChannelById(config.textChannel.general!!).embedMessage {
                                title(":birthday: 誕生日おめでとうございます！")
                                descriptionBuilder {
                                    appendln("今日 $month/$day は ${it.effectiveName} ${discordMember?.asMention.orEmpty()} さんの誕生日です。")
                                    if (twitter?.sn != null) {
                                        append("クソリプでお祝いしましょう！ https://kusoripu.nephy.jp/${twitter.sn}")
                                    }
                                }
                                color(Color.Good)
                                timestamp()
                            }.queue()
                        }

                        lastDate = date
                    }
                } catch (e: Exception) {
                    logger.error(e) { "誕生日のチェック中にエラーが発生しました." }
                } finally {
                    TimeUnit.MINUTES.sleep(1)
                }
            }
        }
    }
}

package jp.nephy.glados.feature.command

import com.jagrosh.jdautilities.command.CommandEvent
import jp.nephy.glados.GLaDOS
import jp.nephy.glados.component.api.niconico.param.RankingCategory
import jp.nephy.glados.component.api.niconico.param.RankingPeriod
import jp.nephy.glados.component.api.niconico.param.RankingType
import jp.nephy.glados.component.helper.Color
import jp.nephy.glados.component.helper.deleteQueue
import jp.nephy.glados.component.helper.embedMention
import jp.nephy.glados.component.helper.prompt.PromptBuilder
import jp.nephy.glados.feature.CommandFeature
import jp.nephy.glados.logger
import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.Member
import net.dv8tion.jda.core.entities.TextChannel
import java.util.concurrent.TimeUnit


class Nico: CommandFeature() {
    companion object {
        fun respondPrompt(guild: Guild, textChannel: TextChannel, member: Member) {
            PromptBuilder.build(textChannel, member) {
                emojiPrompt<RankingType, RankingType>(
                        title = "ニコニコ動画のランキングを再生します",
                        description = "ランキングの種類を選択してください。",
                        color = Color.Niconico,
                        timeoutSec = 30
                ) { rankingType, _, _ ->
                    emojiPrompt<RankingPeriod, RankingPeriod>(
                            title = "ニコニコ動画のランキングを再生します",
                            description = "再生するニコニコ動画 ${rankingType.friendlyName}ランキングの集計期間を選択してください。",
                            color = Color.Niconico,
                            timeoutSec = 30
                    ) { rankingPeriod, _, _ ->
                        enumPrompt(
                                RankingCategory.All,
                                title = "ニコニコ動画のランキングを再生します",
                                description = "再生するニコニコ動画 ${rankingPeriod.friendlyName}${rankingType.friendlyName}ランキングのカテゴリを選択してください。",
                                color = Color.Niconico,
                                timeoutSec = 60
                        ) { rankingCategory, _, _ ->
                            textChannel.embedMention(member) {
                                title("\"ニコニコ動画のランキングを再生します\"")
                                descriptionBuilder {
                                    appendln("${rankingCategory.friendlyName}カテゴリの${rankingPeriod.friendlyName}${rankingType.friendlyName}ランキングを再生します。")
                                    append("まもなく再生されます。")
                                }
                                color(Color.SoundCloud)
                            }.deleteQueue(30, TimeUnit.SECONDS)

                            val bot = GLaDOS.instance
                            val guildPlayer = bot.playerManager.getGuildPlayer(guild)
                            bot.apiClient.niconico.play(guildPlayer, rankingType, rankingPeriod, rankingCategory)

                            logger.info { "サーバ ${guild.name} でニコニコ動画 ${rankingCategory.friendlyName}カテゴリの${rankingPeriod.friendlyName}${rankingType.name}ランキングをキューに追加します." }
                        }
                    }
                }
            }
        }
    }

    init {
        name = "nico"
        aliases = arrayOf("nv")
        help = "ニコニコ動画のランキングを再生します。"

        isMusicCommand = true
        requireSameChannel = true
    }

    override fun executeCommand(event: CommandEvent) {
        Nico.respondPrompt(event.guild, event.textChannel, event.member)
    }
}

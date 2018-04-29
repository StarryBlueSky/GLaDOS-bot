package jp.nephy.glados.feature.command

import com.jagrosh.jdautilities.command.CommandEvent
import jp.nephy.glados.GLaDOS
import jp.nephy.glados.component.api.soundcloud.param.ChartType
import jp.nephy.glados.component.api.soundcloud.param.Genre
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


class SoundCloud: CommandFeature() {
    companion object {
        fun respondPrompt(guild: Guild, textChannel: TextChannel, member: Member) {
            PromptBuilder.build(textChannel, member) {
                emojiPrompt<ChartType, ChartType>(
                        title = "SoundCloudのチャート TOP50を再生します",
                        description = "再生するチャートの種類を選択してください。",
                        color = Color.SoundCloud,
                        timeoutSec = 30
                ) { chartType, _, _ ->
                    enumPrompt(Genre.AllMusic,
                            title = "SoundCloudのチャート TOP50を再生します",
                            description = "再生するSoundCloud ${chartType.name}チャートのジャンルを選択してください。",
                            color = Color.SoundCloud,
                            timeoutSec = 60
                    ) { genre, _, _ ->
                        textChannel.embedMention(member) {
                            title("SoundCloudのチャート TOP50を再生します")
                            descriptionBuilder {
                                appendln("${chartType.name}チャートの${genre.friendlyName}ジャンルのTOP50を再生します。")
                                append("まもなく再生されます。")
                            }
                            color(Color.SoundCloud)
                        }.deleteQueue(30, TimeUnit.SECONDS)

                        val bot = GLaDOS.instance
                        val guildPlayer = bot.playerManager.getGuildPlayer(guild)
                        bot.apiClient.soundCloud.play(guildPlayer, chartType, genre)

                        logger.info { "サーバ ${guild.name} でSoundCloud ${genre.friendlyName}ジャンルの${chartType.friendlyName}チャートをキューに追加します." }
                    }
                }
            }
        }
    }

    init {
        name = "soundcloud"
        aliases = arrayOf("sc")
        help = "SoundCloudのチャートを再生します。"

        isMusicCommand = true
        requireSameChannel = true
    }

    override fun executeCommand(event: CommandEvent) {
        SoundCloud.respondPrompt(event.guild, event.textChannel, event.member)
    }
}

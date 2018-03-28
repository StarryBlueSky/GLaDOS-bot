package jp.nephy.glados.component.helper

import jp.nephy.glados.GLaDOS
import jp.nephy.glados.component.helper.profile.UserProfile
import jp.nephy.glados.component.helper.prompt.PromptBuilder
import jp.nephy.jsonkt.JsonKt
import net.dv8tion.jda.core.entities.Member
import net.dv8tion.jda.core.entities.TextChannel
import net.dv8tion.jda.core.events.Event
import net.gpedro.integrations.slack.SlackApi
import net.gpedro.integrations.slack.SlackMessage
import okhttp3.OkHttpClient
import okhttp3.Request

class FeatureHelper(private val bot: GLaDOS) {
    private val client = OkHttpClient()
    fun getUserProfile(member: Member): UserProfile? {
        val config = bot.config.getGuildConfig(member.guild)
        if (config.option.clientToken == null) {
            return null
        }

        return try {
            val response = client.newCall(
                Request.Builder()
                        .url("https://discordapp.com/api/v6/users/${member.user.id}/profile")
                        .header("Authorization", config.option.clientToken)
                        .build()
            ).execute()
            val content = response.body()!!.string()

            JsonKt.parse(content)
        } catch (e: Exception) {
            bot.logger.error(e) { "プロフィールの取得に失敗しました" }
            null
        }
    }

    fun promptBuilder(textChannel: TextChannel, member: Member, operation: PromptBuilder.() -> Unit) {
        PromptBuilder.build(bot.eventWaiter, textChannel, member, operation)
    }

    fun messageLog(event: Event, title: String? = null, color: Color = Color.Plain, message: () -> Any) {
        val guild = event.nullableGuild
        if (guild == null) {
            bot.logger.warn { "${event.javaClass.simpleName} は guild フィールドを持っていないためMessageロガーは使用できません. 代わりにConsoleロガーを使用します." }
            bot.logger.info(message)
            return
        }

        val config = bot.config.getGuildConfig(guild)
        if (config.textChannel.log == null) {
            bot.logger.warn { "${guild.name} は logチャンネルが未定義のためMessageロガーは使用できません. 代わりにConsoleロガーを使用します." }
            bot.logger.info(message)
            return
        }

        guild.getTextChannelById(config.textChannel.log).embedMessage {
            if (title != null) {
                title(title)
            }
            description(message)
            color(color)
            footer(guild.name, guild.iconUrl)
            timestamp()
        }.queue()
    }

    fun slackLog(event: Event, username: String? = null, channel: String? = null, iconUrl: String? = null, message: () -> Any) {
        val guild = event.nullableGuild
        if (guild == null) {
            bot.logger.warn { "${event.javaClass.simpleName} は guild フィールドを持っていないためSlackロガーは使用できません. 代わりにConsoleロガーを使用します." }
            bot.logger.info { "$username: ${message()}" }
            return
        }
        val config = bot.config.getGuildConfig(guild)

        val member = event.nullableMember
        val user = event.nullableUser
        var iconUrlNew: String? = null
        val usernameNonNull = username ?: if (member != null) {
            iconUrlNew = member.user.effectiveAvatarUrl
            if (config.isMain) {
                member.fullNameWithoutGuild
            } else {
                member.fullName
            }
        } else {
            iconUrlNew = user?.effectiveAvatarUrl
            user?.displayName ?: throw IllegalStateException("適切なユーザ名を補完できませんでした. 手動で指定してください.")
        }

        val channelNonNull = channel ?: if (config.isMain) {
            "#discord"
        } else {
            "#discord-other"
        }

        val msg = SlackMessage(channelNonNull, usernameNonNull, message().toString()).setLinkNames(false)
        if (iconUrl ?: iconUrlNew != null) {
            msg.setIcon(iconUrl ?: iconUrlNew)
        }

        SlackApi(bot.secret.slackIncomingUrl).call(msg)
    }
}

package jp.nephy.glados.feature

import com.jagrosh.jdautilities.command.Command
import com.jagrosh.jdautilities.command.CommandEvent
import jp.nephy.glados.GLaDOS
import jp.nephy.glados.component.audio.music.GuildPlayer
import jp.nephy.glados.component.helper.*
import net.dv8tion.jda.core.entities.ChannelType
import java.util.concurrent.TimeUnit

abstract class CommandFeature(final override val bot: GLaDOS): Command(), IFeature {
    override val helper = FeatureHelper(bot)

    var isMusicCommand = false
    var requireSameChannel = false
    lateinit var guildPlayer: GuildPlayer

    var isAdminCommand = false
    abstract fun executeCommand(event: CommandEvent)

    override fun execute(event: CommandEvent) {
        if (event.channelType == ChannelType.TEXT) {
            val config = bot.config.getGuildConfig(event.guild)
            if (! config.option.useCommand) {
                bot.logger.info { "サーバ ${event.guild.name} ではコマンドは利用不可です。" }
                return
            }
        }

        if (isAdminCommand) {
            if (event.author.idLong != bot.parameter.ownerId) {
                when (event.channelType) {
                    ChannelType.TEXT -> {
                        val config = bot.config.getGuildConfig(event.guild)
                        if (config.role.admin == null) {
                            bot.logger.warn { "Adminロールを要求するコマンド: $name が実行されましたが 管理者ロールが未定義であるため実行しませんでした." }
                            return
                        } else if (!event.member.hasRole(config.role.admin)) {
                            return event.embedMention {
                                title("コマンドエラー: $name")
                                description { "このコマンドの実行には管理者ロールが必要です。" }
                                color(Color.Bad)
                                timestamp()
                            }.deleteQueue(30, TimeUnit.SECONDS, bot.messageCacheManager)
                        }
                    }
                    ChannelType.PRIVATE -> {
                        return event.embedMention {
                            title("コマンドエラー: $name")
                            description { "このコマンドは管理者のみが実行できます。" }
                            color(Color.Bad)
                            timestamp()
                        }.queue()
                    }
                    else -> throw IllegalStateException("不明なチャンネルです. (${event.channelType})")
                }
            }
        }

        if (arguments != null) {
            if (event.args.isBlank()) {
                return event.embedMention {
                    title("コマンドエラー: $name")
                    descriptionBuilder {
                        appendln("コマンドの引数が足りません。")
                        append("実行例: `${event.client.prefix}$name $arguments`")
                    }
                    color(Color.Bad)
                    timestamp()
                }.deleteQueue(30, TimeUnit.SECONDS, bot.messageCacheManager)
            }
        }

        if (isMusicCommand) {
            guildPlayer = bot.playerManager.getGuildPlayer(event.guild)
            if (! event.member.voiceState.inVoiceChannel() || (requireSameChannel && event.member.voiceState.channel != guildPlayer.voiceChannel)) {
                return event.embedMention {
                    title("コマンドエラー: $name")
                    description { "このコマンドを実行するにはボイスチャンネルに参加している必要があります。" }
                    color(Color.Bad)
                    timestamp()
                }.deleteQueue(30, TimeUnit.SECONDS, bot.messageCacheManager)
            }
        }

        executeCommand(event)
    }
}

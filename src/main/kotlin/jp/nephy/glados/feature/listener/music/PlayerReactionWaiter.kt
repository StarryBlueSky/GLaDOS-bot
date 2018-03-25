package jp.nephy.glados.feature.listener.music

import jp.nephy.glados.GLaDOS
import jp.nephy.glados.component.helper.Color
import jp.nephy.glados.component.audio.music.*
import jp.nephy.glados.component.helper.*
import jp.nephy.glados.feature.ListenerFeature
import jp.nephy.glados.feature.command.Nico
import jp.nephy.glados.feature.command.SoundCloud
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

class PlayerReactionWaiter(bot: GLaDOS): ListenerFeature(bot) {
    private var skipForwardVote = 0
    private var clearVote = 0

    override fun onGuildMessageReactionAdd(event: GuildMessageReactionAddEvent) {
        val guildPlayer = bot.playerManager.getGuildPlayer(event.guild)
        if (event.user.isSelf || event.messageIdLong != guildPlayer.eventMessage.lastNowPlayingMessage?.idLong) {
            return
        }
        if (event.guild.selfMember.hasPermission(Permission.MESSAGE_MANAGE)) {
            event.reaction.removeReaction(event.user).queue()
        }

        if (! event.member.voiceState.inVoiceChannel() || event.member.voiceState.channel != guildPlayer.voiceChannel) {
            return event.channel.embedMention(event.member) {
                title("私が再生しているボイスチャンネルに参加していないのでコマンドは実行できません。")
                color(Color.Bad)
            }.deleteQueue(30, TimeUnit.SECONDS, bot.messageCacheManager)
        }

        val emoji = PlayerEmoji.fromEmoji(event.reactionEmote.name) ?: return
        val config = bot.config.getGuildConfig(event.guild)

        when (emoji) {
            PlayerEmoji.Info -> {
                event.channel.embedMention(event.member) {
                    author("♪ 再生中のトラックとキュー")
                    blankField()

                    val playing = guildPlayer.controls.currentTrack
                    val queue = guildPlayer.controls.queue
                    var ms = playing?.remaining ?: 0

                    if (playing != null) {
                        title(playing.info.title, playing.info.uri)
                        descriptionBuilder {
                            if (playing.type != TrackType.UserRequest) {
                                appendln("このトラックは自動再生です。ユーザがリクエストしたトラックではありません。")
                                appendln("曲をリクエストすると このトラックの再生は中断されます。")
                            }
                            appendln("by ${playing.info.author}")
                            append("再生位置: ${playing.position.toMilliSecondString()} / ${playing.duration.toMilliSecondString()}")
                        }

                        if (playing.youtubedl.info?.thumbnailUrl != null) {
                            thumbnail(playing.youtubedl.info?.thumbnailUrl!!)
                        }
                    } else {
                        title("再生中のトラックはありません。")
                    }
                    field("キューに入っているトラック") {
                        "総トラック数: ${queue.size}, 総再生時間: ${queue.sumBy { it.duration }.toMilliSecondString()}"
                    }
                    queue.take(20).forEachIndexed { i, it ->
                        field("#${(i + 1).toString().padEnd(queue.size.charLength)}: ${it.info.title}") {
                            "長さ: ${it.duration.toMilliSecondString()} / 再生までおよそ ${ms.toMilliSecondString()}"
                        }
                        ms += it.duration
                    }
                    if (queue.size > 20) {
                        field("...") { "#20以降のトラックは省略されました。" }
                    }
                    color(Color.Good)
                    timestamp()
                }
            }
            PlayerEmoji.TogglePlayState -> {
                event.channel.embedMention(event.member) {
                    if (guildPlayer.controls.isPlaying) {
                        guildPlayer.controls.pause()
                        title("プレイヤーを一時停止しました。")
                    } else {
                        guildPlayer.controls.resume()
                        title("プレイヤーを再開しました。")
                    }
                    color(Color.Good)
                }
            }
            PlayerEmoji.SkipBack -> {
                if (guildPlayer.controls.isPlaying) {
                    guildPlayer.controls.skipBack()
                    event.channel.embedMention(event.member) {
                        title("現在の曲を最初に巻き戻しました。")
                        color(Color.Good)
                    }
                } else {
                    event.channel.embedMention(event.member) {
                        title("現在再生中の曲はありません。")
                        color(Color.Bad)
                    }
                }
            }
            PlayerEmoji.SeekBack -> {
                if (guildPlayer.controls.isPlaying) {
                    guildPlayer.controls.seekBack(15)
                    event.channel.embedMention(event.member) {
                        title("15秒巻き戻しました。")
                        description { "現在の再生位置は ${guildPlayer.controls.position.toMilliSecondString()} です。" }
                        color(Color.Good)
                    }
                } else {
                    event.channel.embedMention(event.member) {
                        title("現在再生中の曲はありません。")
                        color(Color.Bad)
                    }
                }
            }
            PlayerEmoji.SeekForward -> {
                if (guildPlayer.controls.isPlaying) {
                    guildPlayer.controls.seekForward(15)
                    event.channel.embedMention(event.member) {
                        title("15秒早送りしました。")
                        description { "現在の再生位置は ${guildPlayer.controls.position.toMilliSecondString()} です。" }
                        color(Color.Good)
                    }
                } else {
                    event.channel.embedMention(event.member) {
                        title("現在再生中の曲はありません。")
                        color(Color.Bad)
                    }
                }
            }
            PlayerEmoji.SkipForward -> {
                if (guildPlayer.controls.isPlaying) {
                    if (config.role.admin != null && event.member.hasRole(config.role.admin)) {
                        guildPlayer.controls.skipForward()
                        skipForwardVote = 0
                        event.channel.embedMention(event.member) {
                            title("管理者特権で現在の曲をスキップしました。")
                            color(Color.Good)
                        }
                    } else {
                        val requiredRate = 0.4
                        val voteCount = ++skipForwardVote
                        val vcMembersCount = guildPlayer.voiceChannel.members.count { ! it.user.isBot }
                        val rate = if (vcMembersCount != 0) {
                            1.0 * voteCount / vcMembersCount
                        } else {
                            1.0
                        }

                        if (rate > requiredRate) {
                            guildPlayer.controls.skipForward()
                            skipForwardVote = 0
                            event.channel.embedMention(event.member) {
                                title("スキップ投票が可決したので 現在の曲をスキップしました。")
                                descriptionBuilder {
                                    appendln("${voteCount}票(${rate.times(100).roundToInt()}%)が集まりました。")
                                    append("(Botは除外しています)")
                                }
                                color(Color.Good)
                            }
                        } else {
                            event.channel.embedMention(event.member) {
                                title("スキップに投票しました。")
                                descriptionBuilder {
                                    val needCount = (vcMembersCount * requiredRate).roundToInt() - voteCount

                                    appendln("現在 ${voteCount}票で スキップにはあと${needCount}票必要です。")
                                    append("(Botは除外しています)")
                                }
                                color(Color.Neutral)
                            }
                        }
                    }
                } else {
                    event.channel.embedMention(event.member) {
                        title("現在再生中の曲はありません。")
                        color(Color.Bad)
                    }
                }
            }
            PlayerEmoji.Shuffle -> {
                if (! guildPlayer.controls.isEmptyQueue) {
                    guildPlayer.controls.shuffle()
                    event.channel.embedMention(event.member) {
                        title("曲順をシャッフルしました。")
                        description { "次の曲から反映されます。" }
                        color(Color.Good)
                    }
                } else {
                    event.channel.embedMention(event.member) {
                        title("現在キューは空です。")
                        color(Color.Bad)
                    }
                }
            }
            PlayerEmoji.RepeatTrack -> {
                event.channel.embedMention(event.member) {
                    if (! guildPlayer.controls.isRepeatTrackEnabled) {
                        guildPlayer.controls.enableRepeatTrack()
                        title("リピートを有効にしました。")
                        description { "スキップかリピート解除しない限り同じ曲が再生されます。" }
                        color(Color.Good)
                    } else {
                        guildPlayer.controls.disableRepeatTrack()
                        title("リピートを無効にしました。")
                        color(Color.Bad)
                    }
                }
            }
            PlayerEmoji.RepeatPlaylist -> {
                event.channel.embedMention(event.member) {
                    if (! guildPlayer.controls.isRepeatPlaylistEnabled) {
                        guildPlayer.controls.enableRepeatPlaylist()
                        title("プレイリストリピートを有効にしました。")
                        description { "次の曲から一度再生された曲はキューから削除されません。" }
                        color(Color.Good)
                    } else {
                        guildPlayer.controls.disableRepeatPlaylist()
                        title("プレイリストリピートを無効にしました。")
                        description { "無効にする前に再生していたトラックはキューに残っています。" }
                        color(Color.Bad)
                    }
                }
            }
            PlayerEmoji.Mute -> {
                event.channel.embedMention(event.member) {
                    if (guildPlayer.controls.isMuted) {
                        guildPlayer.controls.unmute()
                        title("ミュート解除しました。")
                        description { "現在のボリュームは ${guildPlayer.controls.volume}%です。" }
                        color(Color.Good)
                    } else {
                        guildPlayer.controls.mute()
                        title("ミュートしました。")
                        description { "引き続き再生は継続されています。" }
                        color(Color.Bad)
                    }
                }
            }
            PlayerEmoji.VolumeDown -> {
                guildPlayer.controls.volumeDown(10)
                event.channel.embedMention(event.member) {
                    title("ボリュームを下げました。")
                    description { "ボリュームを ${guildPlayer.controls.volume}% に変更しました。" }
                    color(Color.Good)
                }
            }
            PlayerEmoji.VolumeUp -> {
                guildPlayer.controls.volumeUp(10)
                event.channel.embedMention(event.member) {
                    title("ボリュームを上げました。")
                    description { "ボリュームを ${guildPlayer.controls.volume}% に変更しました。" }
                    color(Color.Good)
                }
            }
            PlayerEmoji.ToggleAutoPlaylist -> {
                event.channel.embedMention(event.member) {
                    if (guildPlayer.controls.isAutoPlaylistEnabled) {
                        guildPlayer.controls.disableAutoPlaylist()
                        title("オートプレイリストを無効化しました。")
                        color(Color.Bad)
                    } else {
                        guildPlayer.controls.enableAutoPlaylist()
                        title("オートプレイリストを有効化しました。")
                        color(Color.Good)
                    }
                }
            }
            PlayerEmoji.Clear -> {
                if (config.role.admin != null && event.member.hasRole(config.role.admin)) {
                    guildPlayer.controls.clear()
                    clearVote = 0
                    event.channel.embedMention(event.member) {
                        title("管理者特権で再生キューをクリアしました。")
                        color(Color.Good)
                    }
                } else {
                    val requiredRate = 0.4
                    val voteCount = ++clearVote
                    val vcMembersCount = guildPlayer.voiceChannel.members.count { ! it.user.isBot }
                    val rate = if (vcMembersCount != 0) {
                        1.0 * voteCount / vcMembersCount
                    } else {
                        1.0
                    }

                    if (rate > requiredRate) {
                        guildPlayer.controls.clear()
                        clearVote = 0
                        event.channel.embedMention(event.member) {
                            title("再生キューのクリア投票が可決したので 再生キューをクリアしました。")
                            descriptionBuilder {
                                appendln("${voteCount}票(${rate.times(100).roundToInt()}%)が集まりました。")
                                append("(Botは除外しています)")
                            }
                            color(Color.Good)
                        }
                    } else {
                        event.channel.embedMention(event.member) {
                            title("再生キューのクリアに投票しました。")
                            descriptionBuilder {
                                val needCount = (vcMembersCount * requiredRate).roundToInt() - voteCount

                                appendln("現在 ${voteCount}票で スキップにはあと${needCount}票必要です。")
                                append("(Botは除外しています)")
                            }
                            color(Color.Neutral)
                        }
                    }
                }
            }
            PlayerEmoji.SoundCloud -> {
                return SoundCloud.prompt(bot, event.guild, event.channel, event.member)
            }
            PlayerEmoji.NicoRanking -> {
                return Nico.prompt(bot, event.guild, event.channel, event.member)
            }
        }.deleteQueue(30, TimeUnit.SECONDS, bot.messageCacheManager)
    }
}

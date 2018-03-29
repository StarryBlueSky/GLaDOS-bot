package jp.nephy.glados.feature.command

import com.jagrosh.jdautilities.command.CommandEvent
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import jp.nephy.glados.GLaDOS
import jp.nephy.glados.component.api.niconico.model.SearchResult
import jp.nephy.glados.component.audio.music.*
import jp.nephy.glados.component.helper.*
import jp.nephy.glados.feature.CommandFeature
import java.util.concurrent.TimeUnit


class Play(bot: GLaDOS): CommandFeature(bot) {
    init {
        name = "play"
        help = "指定されたURLのメディアを再生します。"

        isMusicCommand = true
        requireSameChannel = true
        arguments = "<検索ワード|動画URL|プレイリストURL>"
    }

    override fun executeCommand(event: CommandEvent) {
        if (event.args.startsWith("http")) {
            guildPlayer.loadTrack(event.args, TrackType.UserRequest, object: PlayerLoadResultHandler {
                override fun onLoadTrack(track: AudioTrack) {
                    event.embedMention {
                        author("トラックが見つかりました")
                        title("\"${track.info.title}\" (${track.info.length.toMilliSecondString()}) を再生キューに追加します")
                        description {
                            if ((guildPlayer.controls.isEmptyQueue && ! guildPlayer.controls.isPlaying) || guildPlayer.controls.currentTrack == track) {
                                "まもなく再生されます。"
                            } else {
                                "再生まであと${guildPlayer.controls.queue.size + 1}曲 (およそ${guildPlayer.controls.totalDuration.toMilliSecondString()})"
                            }
                        }
                        color(Color.Good)
                        timestamp()
                    }.deleteQueue(30, TimeUnit.SECONDS, bot.messageCacheManager)

                    guildPlayer.controls.add(track)

                    bot.logger.info { "${event.member.fullName}が `${track.info.title}` を再生キューに追加しました. (${track.sourceManager.sourceName})" }
                }

                override fun onLoadPlaylist(playlist: AudioPlaylist) {
                    if (playlist.selectedTrack != null) {
                        event.embedMention {
                            author("トラックが見つかりました")
                            title("\"${playlist.selectedTrack.info.title}\" (${playlist.selectedTrack.info.length.toMilliSecondString()}) を再生キューに追加します")
                            description {
                                if ((guildPlayer.controls.isEmptyQueue && ! guildPlayer.controls.isPlaying) || guildPlayer.controls.currentTrack == playlist.selectedTrack) {
                                    "まもなく再生されます。"
                                } else {
                                    "再生まであと${guildPlayer.controls.queue.size + 1}曲 (およそ${guildPlayer.controls.totalDuration.toMilliSecondString()})"
                                }
                            }
                            color(Color.Good)
                            timestamp()
                        }.deleteQueue(30, TimeUnit.SECONDS, bot.messageCacheManager)

                        guildPlayer.controls.add(playlist.selectedTrack)
                    } else {
                        event.embedMention {
                            author("プレイリストが見つかりました")
                            title("プレイリスト \"${playlist.name}\" (${playlist.tracks.size}曲, ${playlist.tracks.totalDuration.toMilliSecondString()}) を再生キューに追加します")
                            playlist.tracks.forEachIndexed { i, audioTrack ->
                                field("\n#${(i + 1).toString().padEnd(playlist.tracks.size.charLength)}") { audioTrack.info.title }
                            }
                            color(Color.Good)
                            timestamp()
                        }.deleteQueue(30, TimeUnit.SECONDS, bot.messageCacheManager)

                        guildPlayer.controls.addAll(playlist.tracks)
                    }
                }

                override fun onNoResult(guildPlayer: GuildPlayer) {
                    event.embedMention {
                        author("エラー")
                        title("`${event.args}` の結果は見つかりませんでした。")
                        color(Color.Bad)
                        timestamp()
                    }.deleteQueue(60, TimeUnit.SECONDS, bot.messageCacheManager)
                }

                override fun onFailed(exception: FriendlyException, guildPlayer: GuildPlayer) {
                    event.embedMention {
                        author("エラー")
                        title("`${event.args}` の読み込みに失敗しました。")
                        description { exception.localizedMessage }
                        color(Color.Bad)
                        timestamp()
                    }.deleteQueue(60, TimeUnit.SECONDS, bot.messageCacheManager)
                }
            })
        } else {
            // "ytsearch:${event.args}"

            guildPlayer.searchTrack(event.args, handler = object: PlayerSearchResultHandler {
                override fun onFoundNiconicoResult(result: SearchResult) {
                    helper.promptBuilder(event.textChannel, event.member) {
                        listPrompt(
                                result.data, result.data.first(), { "${it.title} (${it.lengthSeconds}秒)" }, { "${it.description.take(20)}..." },
                                author = "ニコニコ動画で${result.meta.totalCount}件の動画が見つかりました",
                                title = "\"${event.args}\" の検索結果です",
                                timeoutSec = 30
                        ) { selected, _, _ ->
                            guildPlayer.loadTrack("http://www.nicovideo.jp/watch/${selected.contentId}", TrackType.UserRequest, object: PlayerLoadResultHandler {
                                override fun onLoadTrack(track: AudioTrack) {
                                    track.nicoCacheSetter = selected

                                    event.embedMention {
                                        author("トラックが見つかりました")
                                        title("\"${track.info.title}\" (${track.info.length.toMilliSecondString()}) を再生キューに追加します")
                                        description {
                                            if ((guildPlayer.controls.isEmptyQueue && ! guildPlayer.controls.isPlaying) || guildPlayer.controls.currentTrack == track) {
                                                "まもなく再生されます。"
                                            } else {
                                                "再生まであと${guildPlayer.controls.queue.size + 1}曲 (およそ${guildPlayer.controls.totalDuration.toMilliSecondString()})"
                                            }
                                        }
                                        color(Color.Good)
                                        timestamp()
                                    }.deleteQueue(30, TimeUnit.SECONDS, bot.messageCacheManager)

                                    guildPlayer.controls.add(track)

                                    bot.logger.info { "${event.member.fullName}が `${track.info.title}` を再生キューに追加しました. (${track.sourceManager.sourceName})" }
                                }

                                override fun onNoResult(guildPlayer: GuildPlayer) {
                                    event.embedMention {
                                        author("エラー")
                                        title("`${event.args}` の結果は見つかりませんでした。")
                                        color(Color.Bad)
                                        timestamp()
                                    }.deleteQueue(60, TimeUnit.SECONDS, bot.messageCacheManager)
                                }

                                override fun onFailed(exception: FriendlyException, guildPlayer: GuildPlayer) {
                                    event.embedMention {
                                        author("エラー")
                                        title("`${event.args}` の読み込みに失敗しました。")
                                        description { exception.localizedMessage }
                                        color(Color.Bad)
                                        timestamp()
                                    }.deleteQueue(60, TimeUnit.SECONDS, bot.messageCacheManager)
                                }
                            })
                        }
                    }
                }

                override fun onFoundYouTubeResult(result: List<com.google.api.services.youtube.model.SearchResult>) {
                    helper.promptBuilder(event.textChannel, event.member) {
                        listPrompt(
                                result, result.first(), { it.snippet.title }, { "${it.snippet.description.orEmpty().take(20)}..." },
                                author = "YouTubeで${result.size}件の動画が見つかりました",
                                title = "\"${event.args}\" の検索結果です",
                                timeoutSec = 30
                        ) { selected, _, _ ->
                            guildPlayer.loadTrack("https://www.youtube.com/watch?v=${selected.id.videoId}", TrackType.UserRequest, object: PlayerLoadResultHandler {
                                override fun onLoadTrack(track: AudioTrack) {
                                    track.youtubeCacheSetter = selected

                                    event.embedMention {
                                        author("トラックが見つかりました")
                                        title("\"${track.info.title}\" (${track.info.length.toMilliSecondString()}) を再生キューに追加します")
                                        description {
                                            if ((guildPlayer.controls.isEmptyQueue && ! guildPlayer.controls.isPlaying) || guildPlayer.controls.currentTrack == track) {
                                                "まもなく再生されます。"
                                            } else {
                                                "再生まであと${guildPlayer.controls.queue.size + 1}曲 (およそ${guildPlayer.controls.totalDuration.toMilliSecondString()})"
                                            }
                                        }
                                        color(Color.Good)
                                        timestamp()
                                    }.deleteQueue(30, TimeUnit.SECONDS, bot.messageCacheManager)

                                    guildPlayer.controls.add(track)

                                    bot.logger.info { "${event.member.fullName}が `${track.info.title}` を再生キューに追加しました. (${track.sourceManager.sourceName})" }
                                }

                                override fun onNoResult(guildPlayer: GuildPlayer) {
                                    event.embedMention {
                                        author("エラー")
                                        title("`${event.args}` の結果は見つかりませんでした。")
                                        color(Color.Bad)
                                        timestamp()
                                    }.deleteQueue(60, TimeUnit.SECONDS, bot.messageCacheManager)
                                }

                                override fun onFailed(exception: FriendlyException, guildPlayer: GuildPlayer) {
                                    event.embedMention {
                                        author("エラー")
                                        title("`${event.args}` の読み込みに失敗しました。")
                                        description { exception.localizedMessage }
                                        color(Color.Bad)
                                        timestamp()
                                    }.deleteQueue(60, TimeUnit.SECONDS, bot.messageCacheManager)
                                }
                            })
                        }
                    }
                }
            })
        }
    }
}

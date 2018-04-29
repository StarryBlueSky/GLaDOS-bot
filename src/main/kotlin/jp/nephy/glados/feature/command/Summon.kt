package jp.nephy.glados.feature.command

import com.jagrosh.jdautilities.command.CommandEvent
import jp.nephy.glados.component.helper.Color
import jp.nephy.glados.component.helper.deleteQueue
import jp.nephy.glados.component.helper.embedMention
import jp.nephy.glados.feature.CommandFeature
import java.util.concurrent.TimeUnit


class Summon: CommandFeature() {
    init {
        name = "summon"
        help = "現在接続中のボイスチャンネルにGLaDOSを呼びます。"

        isMusicCommand = true
    }

    override fun executeCommand(event: CommandEvent) {
        if (event.member.voiceState.channel == guildPlayer.voiceChannel) {
            return event.embedMention {
                title("コマンドエラー: $name")
                description { "既に同じボイスチャンネルに参加しています。" }
                color(Color.Bad)
                timestamp()
            }.deleteQueue(30, TimeUnit.SECONDS)
        }

        guildPlayer.joinVoiceChannel(event.member.voiceState.channel)
    }
}

package jp.nephy.glados.component.audio

import jp.nephy.glados.GLaDOS
import jp.nephy.glados.component.audio.music.GuildPlayer
import net.dv8tion.jda.core.audio.AudioReceiveHandler
import net.dv8tion.jda.core.audio.CombinedAudio
import net.dv8tion.jda.core.audio.UserAudio


class AudioRecorder(private val bot: GLaDOS, private val guildPlayer: GuildPlayer): AudioReceiveHandler {
    override fun canReceiveCombined() = true
    override fun handleCombinedAudio(combinedAudio: CombinedAudio) {
        // TODO: Google Cloud Speech APIでいろいろする
    }

    override fun canReceiveUser() = false
    override fun handleUserAudio(userAudio: UserAudio) {}
}

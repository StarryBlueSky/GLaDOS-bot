/*
 * The MIT License (MIT)
 *
 *     Copyright (c) 2017-2019 Nephy Project Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package jp.nephy.glados.clients.discord.listener.receive.events

import jp.nephy.glados.clients.discord.GuildPlayer
import jp.nephy.glados.clients.discord.listener.receive.DiscordReceiveAudioEventSubscription
import net.dv8tion.jda.api.audio.CombinedAudio
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.User

data class DiscordCombinedAudioReceiveEvent(
    override val subscription: DiscordReceiveAudioEventSubscription,
    override val guildPlayer: GuildPlayer,
    val audio: CombinedAudio
): DiscordReceiveAudioEventBase

val DiscordCombinedAudioReceiveEvent.users: List<User>
    get() = audio.users

val DiscordCombinedAudioReceiveEvent.members: List<Member>
    get() = users.map { guild.getMember(it)!! }

fun DiscordCombinedAudioReceiveEvent.audioData(volume: Double): ByteArray {
    return audio.getAudioData(volume)
}

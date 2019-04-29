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

@file:Suppress("UNUSED")

package jp.nephy.glados.clients.discord.config

import jp.nephy.glados.api.ConfigJson
import jp.nephy.glados.api.GLaDOS
import jp.nephy.glados.api.config
import jp.nephy.glados.clients.discord.jda
import jp.nephy.jsonkt.*
import jp.nephy.jsonkt.delegation.*
import net.dv8tion.jda.api.entities.*

val ConfigJson.discord: DiscordConfig
    get() = json["discord"].parseOrNull() ?: throw IllegalStateException("Key \"discord\" is not found in config.json.")

fun DiscordConfig.guild(key: String): GuildConfig {
    return guildMap[key] ?: throw NoSuchElementException("Guild config \"$key\" is not found in config.json.")
}

val Guild?.config: GuildConfig?
    get() {
        if (this == null) {
            return null
        }

        return GLaDOS.config.discord.guilds.find { it.id == idLong }
    }

val DiscordConfig.owner: User?
    get() = ownerId?.let { GLaDOS.jda.getUserById(it) }

fun GuildConfig?.textChannel(key: String): TextChannel? {
    return GLaDOS.jda.getTextChannelById(this?.textChannels?.longValueOrNull(key) ?: return null)
}

fun GuildConfig?.voiceChannel(key: String): VoiceChannel? {
    return GLaDOS.jda.getVoiceChannelById(this?.voiceChannels?.longValueOrNull(key) ?: return null)
}

fun GuildConfig?.role(key: String): Role? {
    return GLaDOS.jda.getRoleById(this?.roles?.longValueOrNull(key) ?: return null)
}

fun GuildConfig?.emote(key: String): Emote? {
    return GLaDOS.jda.getEmoteById(this?.emotes?.longValueOrNull(key) ?: return null)
}

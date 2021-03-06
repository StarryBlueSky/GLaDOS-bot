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

import jp.nephy.glados.api.GLaDOS
import jp.nephy.glados.api.Plugin
import jp.nephy.glados.api.config
import net.dv8tion.jda.api.entities.Emote
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.VoiceChannel
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun Plugin.textChannelsLazy(name: String): ReadOnlyProperty<Plugin, List<TextChannel>> = object: ReadOnlyProperty<Plugin, List<TextChannel>> {
    override fun getValue(thisRef: Plugin, property: KProperty<*>): List<TextChannel> {
        return GLaDOS.config.discord.guilds.mapNotNull { it.textChannel(name) }
    }
}

fun Plugin.voiceChannelsLazy(name: String): ReadOnlyProperty<Plugin, List<VoiceChannel>> = object: ReadOnlyProperty<Plugin, List<VoiceChannel>> {
    override fun getValue(thisRef: Plugin, property: KProperty<*>): List<VoiceChannel> {
        return GLaDOS.config.discord.guilds.mapNotNull { it.voiceChannel(name) }
    }
}

fun Plugin.rolesLazy(name: String): ReadOnlyProperty<Plugin, List<Role>> = object: ReadOnlyProperty<Plugin, List<Role>> {
    override fun getValue(thisRef: Plugin, property: KProperty<*>): List<Role> {
        return GLaDOS.config.discord.guilds.mapNotNull { it.role(name) }
    }
}

fun Plugin.emotesLazy(name: String): ReadOnlyProperty<Plugin, List<Emote>> = object: ReadOnlyProperty<Plugin, List<Emote>> {
    override fun getValue(thisRef: Plugin, property: KProperty<*>): List<Emote> {
        return GLaDOS.config.discord.guilds.mapNotNull { it.emote(name) }
    }
}

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

package jp.nephy.glados.clients.discord.extensions.messages

import net.dv8tion.jda.api.entities.IMentionable
import net.dv8tion.jda.api.EmbedBuilder
import java.awt.Color
import java.time.Instant
import java.time.temporal.TemporalAccessor

fun EmbedBuilder.title(title: String? = null, url: String? = null) = apply {
    setTitle(title, url)
}

fun EmbedBuilder.author(name: String? = null, url: String? = null, iconUrl: String? = null) = apply {
    setAuthor(name, url, iconUrl)
}

fun EmbedBuilder.mention(target: IMentionable) = apply {
    descriptionBuilder.insert(0, "${target.asMention}\n")
}

fun EmbedBuilder.description(description: () -> String) = apply {
    descriptionBuilder.append(description())
}

fun EmbedBuilder.descriptionBuilder(description: StringBuilder.() -> Unit) = apply {
    description(descriptionBuilder)
}

fun EmbedBuilder.image(url: String) = apply {
    setImage(url)
}

fun EmbedBuilder.thumbnail(url: String) = apply {
    setThumbnail(url)
}

fun EmbedBuilder.footer(text: String? = null, iconUrl: String? = null) = apply {
    setFooter(text, iconUrl)
}

fun EmbedBuilder.timestamp(temporal: TemporalAccessor? = null) = apply {
    setTimestamp(temporal ?: Instant.now())
}

fun EmbedBuilder.color(color: Color) = apply {
    setColor(color)
}

fun EmbedBuilder.blankField(inline: Boolean = false) = apply {
    addBlankField(inline)
}

fun EmbedBuilder.field(name: String? = null, inline: Boolean = false, value: () -> Any?) = apply {
    addField(name, value()?.toString(), inline)
}

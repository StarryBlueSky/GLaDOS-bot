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

package jp.nephy.glados.clients.discord.command.events

import jp.nephy.glados.api.Event
import jp.nephy.glados.clients.discord.command.DiscordCommandSubscription
import net.dv8tion.jda.api.entities.*

/**
 * DiscordCommandEvent.
 */
open class DiscordCommandEvent(
    final override val subscription: DiscordCommandSubscription,

    /**
     * Arguments of this command.
     */
    val arguments: List<String>,

    /**
     * The [Message] object.
     */
    val message: Message,

    /**
     * Author.
     */
    val author: User,

    /**
     * Guild member.
     */
    open val member: Member?,

    /**
     * Guild.
     */
    open val guild: Guild?,

    /**
     * Message channel.
     */
    open val channel: MessageChannel
): Event

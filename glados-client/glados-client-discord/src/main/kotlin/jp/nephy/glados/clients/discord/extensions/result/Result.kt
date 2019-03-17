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

package jp.nephy.glados.clients.discord.extensions.result

import jp.nephy.glados.clients.discord.command.events.DiscordCommandEvent
import jp.nephy.glados.clients.discord.command.primaryCommandSyntax
import jp.nephy.glados.clients.discord.extensions.messages.HexColor
import jp.nephy.glados.clients.discord.extensions.messages.message
import jp.nephy.glados.clients.discord.extensions.messages.reply
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.requests.restaction.MessageAction

fun Message.embedResult(commandName: String, description: () -> String): MessageAction {
    return reply {
        embed {
            title(commandName)
            description(description)
            timestamp()
            color(HexColor.Good)
        }
    }
}

fun Message.simpleResult(description: () -> String): MessageAction {
    return message {
        text {
            append("${author.asMention} ${description.invoke()}")
        }
    }
}

fun DiscordCommandEvent.embedResult(description: () -> String): MessageAction {
    return message.embedResult(subscription.primaryCommandSyntax, description)
}

fun DiscordCommandEvent.simpleResult(description: () -> String): MessageAction {
    return message.simpleResult(description)
}

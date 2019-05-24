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

package jp.nephy.glados.clients.discord

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.message.MessageUpdateEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.util.concurrent.ConcurrentHashMap

/**
 * DiscordMessageManager.
 */
object DiscordMessageManager {
    private val cache = ConcurrentHashMap<Long, Message>()
    private val history = ConcurrentHashMap<Long, Message>()

    fun add(message: Message) {
        cache[message.idLong] = message
    }

    fun latest(id: Long): Message? {
        return cache[id]
    }

    fun history(id: Long): Message? {
        return history[id]
    }

    fun filter(predicate: (Message) -> Boolean): List<Message> {
        return cache.values.filter(predicate)
    }
    
    internal object Listener: ListenerAdapter() {
        override fun onMessageReceived(event: MessageReceivedEvent) {
            add(event.message)
        }

        override fun onMessageUpdate(event: MessageUpdateEvent) {
            cache[event.message.idLong]?.also {
                history[event.message.idLong] = it
            }
            cache[event.message.idLong] = event.message
        }
    }
}

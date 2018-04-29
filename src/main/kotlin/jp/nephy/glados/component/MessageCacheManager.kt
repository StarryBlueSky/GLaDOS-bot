package jp.nephy.glados.component

import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import net.dv8tion.jda.core.events.message.MessageUpdateEvent

class MessageCacheManager {
    private val cache = mutableMapOf<Long, Message>()

    fun add(event: MessageReceivedEvent) {
        cache[event.messageIdLong] = event.message
    }

    fun add(message: Message) {
        cache[message.idLong] = message
    }

    fun update(event: MessageUpdateEvent) {
        cache[event.messageIdLong] = event.message
    }

    fun get(id: Long): Message? {
        return cache[id]
    }

    fun delete(filter: (Message) -> Boolean) {
        cache.values.filter(filter).forEach { it.delete().queue({}, {}) }
    }
}

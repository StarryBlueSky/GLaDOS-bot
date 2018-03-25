package jp.nephy.glados.component

import jp.nephy.glados.GLaDOS
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import net.dv8tion.jda.core.events.message.MessageUpdateEvent
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class MessageCacheManager(private val bot: GLaDOS) {
    private val cache = mutableMapOf<Long, Message>()

    init {
        thread(name = "Check Messages Cache") {
            while (true) {
                TimeUnit.MINUTES.sleep(15)
                bot.logger.debug { "メッセージキャッシュ数: ${cache.size}" }
            }
        }
    }

    fun add(event: MessageReceivedEvent) {
        cache[event.messageIdLong] = event.message
    }
    fun add(message: Message) {
        cache[message.idLong] = message
    }
    fun update(event: MessageUpdateEvent) {
        cache[event.messageIdLong] = event.message
    }
    fun contains(id: Long): Boolean {
        return cache.contains(id)
    }
    fun get(id: Long, remove: Boolean = true): Message? {
        return if (remove) {
            cache.remove(id)
        } else {
            cache[id]
        }
    }
    fun delete(filter: (Message) -> Boolean) {
        cache.values.filter(filter).forEach { it.delete().queue() }
    }
}

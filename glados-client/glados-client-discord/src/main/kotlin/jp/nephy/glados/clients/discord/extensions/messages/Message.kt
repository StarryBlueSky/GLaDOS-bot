package jp.nephy.glados.clients.discord.extensions.messages

import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.requests.restaction.MessageAction

fun MessageChannel.message(block: SendMessageWrapper.() -> Unit): MessageAction {
    return SendMessageWrapper(this).apply(block).build()
}

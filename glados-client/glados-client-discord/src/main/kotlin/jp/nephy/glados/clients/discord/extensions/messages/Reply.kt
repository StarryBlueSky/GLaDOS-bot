package jp.nephy.glados.clients.discord.extensions.messages

import jp.nephy.glados.clients.discord.command.events.DiscordCommandEvent
import jp.nephy.glados.clients.discord.extensions.messages.wrapper.SendMessageWrapper
import net.dv8tion.jda.api.entities.IMentionable
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.message.MessageUpdateEvent
import net.dv8tion.jda.api.requests.restaction.MessageAction

fun DiscordCommandEvent.reply(block: SendMessageWrapper.() -> Unit): MessageAction {
    return SendMessageWrapper(channel, author).apply(block).build()
}

fun MessageReceivedEvent.reply(block: SendMessageWrapper.() -> Unit): MessageAction {
    return SendMessageWrapper(channel, author).apply(block).build()
}

fun MessageUpdateEvent.reply(block: SendMessageWrapper.() -> Unit): MessageAction {
    return SendMessageWrapper(channel, author).apply(block).build()
}

fun Message.reply(block: SendMessageWrapper.() -> Unit): MessageAction {
    return SendMessageWrapper(channel, author).apply(block).build()
}

fun MessageChannel.reply(target: IMentionable, block: SendMessageWrapper.() -> Unit): MessageAction {
    return SendMessageWrapper(this, target).apply(block).build()
}

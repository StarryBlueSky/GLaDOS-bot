package jp.nephy.glados.clients.discord.extensions.messages

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.requests.restaction.MessageAction

fun MessageChannel.message(block: SendMessageWrapper.() -> Unit): MessageAction {
    return SendMessageWrapper(this).apply(block).build()
}

fun MessageChannel.sendEmbed(block: EmbedBuilder.() -> Unit): MessageAction {
    return sendMessage(EmbedBuilder().apply(block).build())
}

fun EmbedBuilder.title(title: String, url: String? = null) {
    setTitle(title, url)
}

fun EmbedBuilder.author(name: String, url: String? = null, iconUrl: String? = null) {
    setAuthor(name, url, iconUrl)
}

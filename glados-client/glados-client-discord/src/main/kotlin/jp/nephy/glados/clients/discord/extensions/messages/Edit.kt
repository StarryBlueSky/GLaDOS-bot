package jp.nephy.glados.clients.discord.extensions.messages

import jp.nephy.glados.clients.discord.extensions.messages.wrapper.EditMessageWrapper
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.requests.restaction.MessageAction

fun Message.edit(block: EditMessageWrapper.() -> Unit): MessageAction {
    return EditMessageWrapper(this).apply(block).build()
}

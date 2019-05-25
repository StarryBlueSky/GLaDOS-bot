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

package jp.nephy.glados.clients.discord.command

import jp.nephy.glados.GLaDOSSubscriptionClient
import jp.nephy.glados.api.Plugin
import jp.nephy.glados.clients.*
import jp.nephy.glados.clients.discord.command.events.DiscordCommandEvent
import jp.nephy.glados.clients.discord.command.events.DiscordGuildCommandEvent
import jp.nephy.glados.clients.discord.command.events.DiscordPrivateMessageCommandEvent
import jp.nephy.glados.clients.discord.command.policy.CasePolicy
import jp.nephy.glados.clients.discord.command.policy.MessageContentPolicy
import jp.nephy.glados.clients.discord.command.policy.checkAllPolicies
import jp.nephy.glados.clients.discord.extensions.awaitAndDelete
import jp.nephy.glados.clients.discord.extensions.messages.HexColor
import jp.nephy.glados.clients.discord.extensions.messages.emojiEnumPrompt
import jp.nephy.glados.clients.discord.extensions.messages.guildOrNull
import jp.nephy.glados.clients.discord.extensions.messages.reply
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.message.MessageUpdateEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf

/**
 * DiscordCommandSubscriptionClient.
 */
object DiscordCommandSubscriptionClient: GLaDOSSubscriptionClient<DiscordCommand, DiscordCommandEvent, DiscordCommandSubscription>() {
    override fun create(plugin: Plugin, function: KFunction<*>, eventClass: KClass<*>): DiscordCommandSubscription? {
        if (!eventClass.isSubclassOf(DiscordCommandEvent::class)) {
            return null
        }

        val annotation = function.findAnnotation<DiscordCommand>()
        if (annotation == null) {
            logger.warn { "関数: \"${plugin.fullName}#${function.name}\" は @DiscordCommand が付与されていません。スキップします。" }
            return null
        }
        
        return DiscordCommandSubscription(plugin, function, annotation).also { subscription ->
            if (subscription.description == null) {
                logger.warn { "DiscordCommand: ${subscription.fullName} は description が設定されていません。" }
            }
        }
    }
    
    internal object Listener: ListenerAdapter() {
        override fun onMessageReceived(event: MessageReceivedEvent) {
            if (event.message.type != MessageType.DEFAULT) {
                return
            }
            
            launch {
                handleMessage(event.message, event.author, event.member, event.guildOrNull, event.channel)
            }
        }

        override fun onMessageUpdate(event: MessageUpdateEvent) {
            if (event.message.type != MessageType.DEFAULT) {
                return
            }
            
            launch {
                handleMessage(event.message, event.author, event.member, event.guildOrNull, event.channel)
            }
        }

        private val spaceRegex = "\\s+".toRegex()
        
        private fun DiscordCommandSubscription.parseArguments(message: Message): List<String>? {
            val content = when (annotation.content) {
                MessageContentPolicy.Display -> message.contentDisplay
                MessageContentPolicy.Stripped -> message.contentStripped
                MessageContentPolicy.Raw -> message.contentRaw
            }.trim()
            
            val commandLine = content.split(spaceRegex)
            val syntax = commandLine.firstOrNull() ?: return null

            val matched = when (annotation.case) {
                CasePolicy.Strict -> {
                    commandSyntaxes.any { it == syntax }
                }
                CasePolicy.Ignore -> {
                    commandSyntaxes.any { it.equals(syntax, true) }
                }
            }

            if (!matched) {
                return null
            }

            return commandLine.drop(1)
        }
        
        private suspend fun handleMessage(message: Message, author: User, member: Member?, guild: Guild?, channel: MessageChannel) {
            for (subscription in subscriptions) {
                val arguments = subscription.parseArguments(message) ?: continue
                val event = when (channel) {
                    is TextChannel -> DiscordGuildCommandEvent(subscription, arguments, message, author, member!!, guild!!, channel)
                    is PrivateChannel -> DiscordPrivateMessageCommandEvent(subscription, arguments, author, message, channel)
                    else -> throw UnsupportedOperationException("Unknown channel: ${channel::class.qualifiedName}.")
                }
                
                if (!subscription.eventClass.isInstance(event)) {
                    continue
                }

                if (!event.checkAllPolicies()) {
                    continue
                }
                
                if (subscription.isExperimental) {
                    event.message.emojiEnumPrompt<ExperimentalConsent> {
                        title("`${event.subscription.primaryCommandSyntax}`")
                        description { "⚠ この機能は現在 試験中(Experimental) です。予期しない不具合が発生する可能性がありますが, ご理解の上ご利用ください。" }
                        color(HexColor.Change)
                        timeout(30, TimeUnit.SECONDS)
                    }.onSuccess {
                        if (it.selected == ExperimentalConsent.Agree) {
                            subscription.invoke(event)
                            subscription.logger.info { "同意したので実行されました。(${guild?.name})" }
                        } else {
                            event.message.reply {
                                embed {
                                    title("`${event.subscription.primaryCommandSyntax}`")
                                    description { "キャンセルしました。" }
                                    color(HexColor.Bad)
                                    timestamp()
                                }
                            }.awaitAndDelete(15, TimeUnit.SECONDS)
                        }
                    }
                } else {
                    subscription.invoke(event)
                    subscription.logger.info { "実行されました。(${guild?.name})" }
                }

                return
            }
        }
    }
}

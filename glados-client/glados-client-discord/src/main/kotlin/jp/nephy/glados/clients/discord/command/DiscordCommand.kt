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

import jp.nephy.glados.api.Priority
import jp.nephy.glados.clients.discord.command.policy.*

/**
 * Indicates that this function is executed as [DiscordCommandSubscription].
 */
@Target(AnnotationTarget.FUNCTION)
annotation class DiscordCommand(
    /**
     * 
     */
    val command: String = "",

    /**
     * 
     */
    val aliases: Array<String> = [],

    /**
     * 
     */
    val permission: PermissionPolicy = PermissionPolicy.Anyone,

    /**
     * 
     */
    val channelType: ChannelTypePolicy = ChannelTypePolicy.Any,

    /**
     * 
     */
    val case: CasePolicy = CasePolicy.Ignore,

    /**
     * 
     */
    val condition: VoiceChannelConditionPolicy = VoiceChannelConditionPolicy.Anytime,

    /**
     * 
     */
    val content: MessageContentPolicy = MessageContentPolicy.Display,
    
    /**
     * 
     */
    val description: String = "",

    /**
     * 
     */
    val arguments: Array<String> = [],

    /**
     * 
     */
    val checkArgumentsSize: Boolean = true,

    /**
     * 
     */
    val prefix: String = "",

    /**
     * 
     */
    val category: String = "",

    /**
     * 
     */
    val allowFromSelfUser: Boolean = false,

    /**
     * 
     */
    val allowFromBotAccount: Boolean = false,

    /**
     * Execution priority.
     */
    val priority: Priority = Priority.Normal
)

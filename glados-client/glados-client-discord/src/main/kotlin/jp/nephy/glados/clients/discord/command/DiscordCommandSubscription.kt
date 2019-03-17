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

import jp.nephy.glados.GLaDOSSubscription
import jp.nephy.glados.api.Plugin
import jp.nephy.glados.api.Priority
import jp.nephy.glados.api.stackTraceString
import jp.nephy.glados.clients.discord.command.error.DiscordCommandError
import jp.nephy.glados.clients.discord.command.events.DiscordCommandEvent
import jp.nephy.glados.clients.discord.command.events.argumentString
import jp.nephy.glados.clients.discord.extensions.launchAndDelete
import jp.nephy.glados.clients.discord.extensions.messages.HexColor
import jp.nephy.glados.clients.discord.extensions.messages.reply
import java.util.concurrent.TimeUnit
import kotlin.reflect.KFunction

/**
 * DiscordCommandSubscription.
 */
class DiscordCommandSubscription(
    override val plugin: Plugin,
    override val function: KFunction<*>,
    override val annotation: DiscordCommand
): GLaDOSSubscription<DiscordCommand, DiscordCommandEvent>() {
    override val priority: Priority
        get() = annotation.priority

    override fun onFailure(throwable: Throwable, event: DiscordCommandEvent) {
        if (throwable is DiscordCommandError) {
            logger.error(throwable) { "コマンドエラーが発生しました。" }
        } else {
            event.reply {
                embed {
                    title("`${event.subscription.primaryCommandSyntax}` の実行中に例外が発生しました")
                    description { "引数: `${event.argumentString}`\nご不便をお掛けしています。この問題が何度も発生する場合は開発者にご連絡ください。" }
                    field("スタックトレース") { "${throwable.stackTraceString.take(300)}..." }
                    color(HexColor.Bad)
                    timestamp()
                }
            }.launchAndDelete(30, TimeUnit.SECONDS)

            super.onFailure(throwable, event)
        }
    }
}

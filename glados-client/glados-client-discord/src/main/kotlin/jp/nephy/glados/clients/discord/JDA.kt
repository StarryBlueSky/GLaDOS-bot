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

package jp.nephy.glados.clients.discord

import jp.nephy.glados.api.GLaDOS
import jp.nephy.glados.api.attributes
import jp.nephy.glados.api.config
import jp.nephy.glados.api.getOrPut
import jp.nephy.glados.clients.discord.command.DiscordCommandSubscriptionClient
import jp.nephy.glados.clients.discord.config.discord
import jp.nephy.glados.clients.discord.listener.websocket.DiscordWebsocketEventSubscriptionClient
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.AccountType
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.entities.Activity

private const val jdaKey = "jda"

val GLaDOS.Companion.jda: JDA
    get() = runBlocking {
        GLaDOS.attributes.read<JDA>(jdaKey)
    }

internal suspend fun initializeJDA() {
    GLaDOS.attributes.getOrPut(jdaKey) {
        JDABuilder(AccountType.BOT).apply {
            setToken(GLaDOS.config.discord.token)

            setStatus(OnlineStatus.DO_NOT_DISTURB)
            setActivity(Activity.playing(GLaDOS.config.discord.defaultActivity))

            setEnableShutdownHook(GLaDOS.config.discord.enableJDAShutdownHook)
            
            // setAudioSendFactory(NativeAudioSendFactory(5000))
            
            addEventListeners(
                DiscordWebsocketEventSubscriptionClient.Listener,
                DiscordCommandSubscriptionClient.Listener,
                DiscordMessageManager.Listener,
                DiscordEventWaiter.Listener
            )
        }.build()
    }
}

internal suspend fun disposeJDA() {
    GLaDOS.attributes.remove<JDA>(jdaKey)?.also { 
        it.shutdown()
    }
}

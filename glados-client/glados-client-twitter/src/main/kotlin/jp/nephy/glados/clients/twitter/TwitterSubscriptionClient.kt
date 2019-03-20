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

package jp.nephy.glados.clients.twitter

import io.ktor.client.engine.cio.CIO
import io.ktor.util.KtorExperimentalAPI
import jp.nephy.glados.GLaDOSSubscriptionClient
import jp.nephy.glados.api.Plugin
import jp.nephy.glados.api.Priority
import jp.nephy.glados.clients.runEvent
import jp.nephy.glados.clients.subscriptions
import jp.nephy.glados.clients.twitter.config.TwitterAccount
import jp.nephy.glados.clients.twitter.config.client
import jp.nephy.glados.clients.twitter.event.*
import jp.nephy.jsonkt.*
import jp.nephy.penicillin.endpoints.stream
import jp.nephy.penicillin.extensions.endpoints.TweetstormListener
import jp.nephy.penicillin.extensions.endpoints.tweetstorm
import jp.nephy.penicillin.extensions.listen
import jp.nephy.penicillin.models.DirectMessage
import jp.nephy.penicillin.models.Status
import jp.nephy.penicillin.models.Stream
import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf

/**
 * TwitterSubscriptionClient.
 */
@Suppress("UNUSED")
object TwitterSubscriptionClient: GLaDOSSubscriptionClient<TwitterEvent, TwitterEventBase, TwitterSubscription>() {
    override val priority: Priority
        get() = Priority.Highest
    
    override fun create(plugin: Plugin, function: KFunction<*>, eventClass: KClass<*>): TwitterSubscription? {
        if (!eventClass.isSubclassOf(TwitterEventBase::class)) {
            return null
        }

        val annotation = function.findAnnotation() ?: defaultTwitterEventAnnotation
        return TwitterSubscription(plugin, function, annotation)
    }
    
    override fun start() {
        subscriptions.flatMap { it.accounts }.distinct().forEach { account ->
            account.startJob()
        }
    }

    override fun stop() {
        for (account in jobs.keys) {
            account.stopJob()
        }
    }

    private val jobs = ConcurrentHashMap<TwitterAccount, Job>()
    
    @UseExperimental(KtorExperimentalAPI::class)
    private fun TwitterAccount.startJob() {
        jobs[this] = launch {
            while (isActive) {
                try {
                    client(engine = CIO).use {
                        // TODO: Change UserStream endpoint
                        it.stream.tweetstorm().listen(createListener(this@startJob)).await(reconnect = false)
                    }
                } catch (e: CancellationException) {
                    delay(5000)
                } catch (e: Throwable) {
                    logger.error(e) { "例外が発生しました。(@${user.screenName})" }
                }
            }

            logger.trace { "終了しました。(@${user.screenName})" }
        }

        logger.trace { "開始しました。(@${user.screenName})" }
    }

    
    
    private fun TwitterAccount.stopJob() {
        jobs[this]?.cancel()
    }

    override fun onSubscriptionLoaded(subscription: TwitterSubscription) {
        subscription.accounts.filter { account ->
            !jobs.containsKey(account) 
        }.forEach { account ->
            account.startJob()
        }
    }

    override fun onSubscriptionUnloaded(subscription: TwitterSubscription) {
        subscription.accounts.filter {  account ->
            subscriptions.none { account in it.accounts }
        }.forEach { account ->
            account.stopJob()
        }
    }
    
    private fun createListener(account: TwitterAccount) = object: TweetstormListener {
        private val filter: (TwitterSubscription) -> Boolean = { account in it.accounts }
        
        override suspend fun onConnect() {
            runEvent(filter) {
                TwitterConnectEvent(account, it)
            }

            logger.info { "Tweetstorm (@${account.user.screenName}) が開始されました。" }
        }

        override suspend fun onDisconnect(cause: Throwable?) {
            runEvent(filter) {
                TwitterDisconnectEvent(account, it, cause)
            }

            if (cause != null) {
                logger.warn(cause) { "Tweetstorm (@${account.user.screenName}) から切断されました。" }
            } else {
                logger.warn { "Tweetstorm (@${account.user.screenName}) から切断されました。" }
            }
        }

        override suspend fun onStatus(status: Status) {
            runEvent(filter) {
                TwitterStatusEvent(account, it, status)
            }
        }

        override suspend fun onDirectMessage(message: DirectMessage) {
            runEvent(filter) {
                TwitterDirectMessageEvent(account, it, message)
            }
        }

        override suspend fun onFriends(friends: Stream.Friends) {
            runEvent(filter) {
                TwitterFriendsEvent(account, it, friends)
            }
        }

        override suspend fun onDelete(delete: Stream.Delete) {
            runEvent(filter) {
                TwitterDeleteEvent(account, it, delete)
            }
        }

        override suspend fun onHeartbeat() {
            runEvent(filter) {
                TwitterHeartbeatEvent(account, it)
            }
        }

        override suspend fun onLength(length: Int) {
            runEvent(filter) {
                TwitterLengthEvent(account, it, length)
            }
        }

        override suspend fun onAnyJson(json: JsonObject) {
            runEvent(filter) {
                TwitterAnyJsonEvent(account, it, json)
            }
        }

        override suspend fun onUnhandledJson(json: JsonObject) {
            runEvent(filter) {
                TwitterUnhandledJsonEvent(account, it, json)
            }
        }

        override suspend fun onUnknownData(data: String) {
            runEvent(filter) {
                TwitterUnknownDataEvent(account, it, data)
            }
        }

        override suspend fun onRawData(data: String) {
            runEvent(filter) {
                TwitterRawDataEvent(account, it, data)
            }
        }
    }
}

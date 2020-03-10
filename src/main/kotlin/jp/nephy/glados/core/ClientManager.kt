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

package jp.nephy.glados.core

import io.ktor.utils.io.core.Closeable
import jp.nephy.glados.api.Logger
import jp.nephy.glados.api.SubscriptionClient
import jp.nephy.glados.api.of
import jp.nephy.glados.clients.name
import java.util.concurrent.CopyOnWriteArraySet
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.system.measureTimeMillis

internal object ClientManager: Closeable {
    private val logger = Logger.of("GLaDOS.ClientManager")
    
    val clients = CopyOnWriteArraySet<SubscriptionClient<*, *, *>>()
    
    fun load() {
        check(clients.isEmpty()) {
            "既に SubscriptionClient はロードされているため, 一括ロードできません。"
        }

        val loadingTimeMillis = measureTimeMillis {
            loadClassesFromClassPath<SubscriptionClient<*, *, *>>().forEach {
                load(it)
            }
        }
        
        logger.info { "$loadingTimeMillis ms で SubscriptionClient のロードが完了しました。" }
    }

    private fun load(kotlinClass: KClass<out SubscriptionClient<*, *, *>>) {
        kotlinClass.runCatching {
            objectInstance ?: createInstance()
        }.onSuccess { client ->
            clients += client
            logger.info { "SubscriptionClient: \"${client.name}\" をロードしました。" }
        }.onFailure { e ->
            logger.error(e) { "クラス: \"${kotlinClass.qualifiedName}\" の初期化に失敗しました。" }
        }
    }
    
    fun start() {
        clients.sortedBy { 
            it.priority
        }.forEach { client ->
            client.runCatching {
                start()
            }.onSuccess {
                client.logger.info { "開始しました。" }
            }.onFailure { e ->
                client.logger.error(e) { "開始中に例外が発生しました。" }
            }
        }
    }

    override fun close() {
        logger.info { "ClientManager を停止します。" }

        clients.sortedBy {
            it.priority
        }.forEach { client ->
            client.runCatching {
                stop()
            }.onSuccess {
                client.logger.info { "停止しました。" }
            }.onFailure { e ->
                client.logger.error(e) { "停止中に例外が発生しました。" }
            }
        }

        clients.clear()
    }
}

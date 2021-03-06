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

package jp.nephy.glados

import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.UserAgent
import jp.nephy.glados.api.GLaDOS
import jp.nephy.glados.api.config
import jp.nephy.glados.api.initialize
import jp.nephy.glados.clients.logger.installHttpClientLogger
import jp.nephy.glados.core.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.newFixedThreadPoolContext
import java.util.concurrent.Executors

internal object InternalCoroutineScope: CoroutineScope {
    override val coroutineContext = Executors.newCachedThreadPool().asCoroutineDispatcher()
}

/**
 * Starts GLaDOS application.
 */
@Suppress("DEPRECATION")
@UseExperimental(ObsoleteCoroutinesApi::class)
fun main(args: Array<String>) {
    GLaDOS.initialize {
        isDevelopmentMode = "--dev" in args
        
        config = ConfigJsonImpl.load()
        secret = SecretJsonImpl.load()
        
        coroutineContext = newFixedThreadPoolContext(config.parallelism, "GLaDOS")

        httpClient = HttpClient(Apache) {
            install(UserAgent) {
                agent = GLaDOS.config.userAgent
            }

            installHttpClientLogger()
        }
        
        fileSystemWatcher = FileSystemWatcherImpl
        
        attributes = AttributeMapImpl
    }

    ClientManager.load()
    PluginManager.loadAll()
    
    ClientManager.start()
    
    ConfigJsonImpl.installFileSystemListener()
    
    FileSystemWatcherImpl.start()
}

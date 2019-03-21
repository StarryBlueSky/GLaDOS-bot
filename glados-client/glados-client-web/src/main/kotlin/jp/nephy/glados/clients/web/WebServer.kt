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

@file:Suppress("UNUSED")

package jp.nephy.glados.clients.web

import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.AutoHeadResponse
import io.ktor.features.DefaultHeaders
import io.ktor.features.StatusPages
import io.ktor.features.XForwardedHeaderSupport
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.sessions.SessionStorageMemory
import io.ktor.sessions.Sessions
import io.ktor.sessions.cookie
import jp.nephy.glados.api.*
import jp.nephy.glados.clients.invoke
import jp.nephy.glados.clients.subscriptions
import jp.nephy.glados.clients.web.config.web
import jp.nephy.glados.clients.web.cookie.WebCookieSetupEvent
import jp.nephy.glados.clients.web.cookie.WebCookieSetupSubscriptionClient
import jp.nephy.glados.clients.web.error.WebErrorEvent
import jp.nephy.glados.clients.web.error.WebErrorPageSubscriptionClient
import jp.nephy.glados.clients.web.error.domain
import jp.nephy.glados.clients.web.error.statuses
import jp.nephy.glados.clients.web.extensions.effectiveHost
import jp.nephy.glados.clients.web.features.DynamicResolver
import jp.nephy.glados.clients.web.features.robotsTxt
import jp.nephy.glados.clients.web.features.sitemapXml
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit

private const val webServerKey = "web"

/**
 * Access to [ApplicationEngine] instance.
 */
val GLaDOS.Companion.webServer: ApplicationEngine
    get() = GLaDOS.attributes[webServerKey]

private val logger = Logger.of("GLaDOS.Web")

internal fun initializeWebApplication() {
    GLaDOS.attributes.getOrPut(webServerKey) {
        embeddedServer(Netty, host = GLaDOS.config.web.host, port = GLaDOS.config.web.port) {
            // Headers
            install(XForwardedHeaderSupport)
            install(DefaultHeaders) {
                header(HttpHeaders.Server, "GLaDOS-bot")
                header("X-Powered-By", "GLaDOS-bot (+https://github.com/NephyProject/GLaDOS-bot)")
            }

            // Error handling
            install(StatusPages) {
                exception<Throwable> { e ->
                    logger.error(e) { "Web サーバで例外が発生しました。" }
                    
                    call.respond(HttpStatusCode.InternalServerError)
                }
                
                for (status in WebErrorPageSubscriptionClient.subscriptions.flatMap { it.statuses }.distinct()) {
                    status(status) {
                        val subscription = WebErrorPageSubscriptionClient.subscriptions.firstOrNull {
                            status in it.statuses && (it.domain == null || it.domain == call.request.effectiveHost)
                        }
                        
                        if (subscription != null) {
                            val event = WebErrorEvent(subscription, this, status)
                            subscription.invoke(event)
                        } else {
                            call.respond(status)
                        }
                    }
                }
            }
            
            // Routing
            install(DynamicResolver)
            install(Routing) {
                sitemapXml()
                robotsTxt()
            }
            if (GLaDOS.config.web.enableHeadRequestHandler) {
                install(AutoHeadResponse)
            }

            // Cookie
            install(Sessions) {
                runBlocking {
                    for (subscription in WebCookieSetupSubscriptionClient.subscriptions) {
                        cookie(subscription.annotation.name, subscription.annotation.sessionClass, SessionStorageMemory()) {
                            val event = WebCookieSetupEvent(subscription, this)
                            subscription.invoke(event)
                        }
                    }
                }
            }
        }.start(wait = false)
    }
}

internal fun disposeWebApplication() {
    GLaDOS.attributes.remove<ApplicationEngine>(webServerKey)?.also {
        it.stop(1, 5, TimeUnit.SECONDS)
    }
}

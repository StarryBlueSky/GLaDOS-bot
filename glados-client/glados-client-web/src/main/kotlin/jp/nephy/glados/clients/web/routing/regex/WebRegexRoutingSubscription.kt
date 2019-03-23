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

package jp.nephy.glados.clients.web.routing.regex

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.request.httpMethod
import io.ktor.request.path
import io.ktor.util.pipeline.PipelineContext
import jp.nephy.glados.GLaDOSSubscription
import jp.nephy.glados.api.Plugin
import jp.nephy.glados.api.Priority
import jp.nephy.glados.clients.web.HttpMethod
import jp.nephy.glados.clients.web.effectiveHost
import jp.nephy.glados.clients.web.glados
import jp.nephy.glados.clients.web.routing.RoutingHandler
import kotlin.reflect.KFunction

/**
 * WebRegexRoutingSubscription.
 */
data class WebRegexRoutingSubscription(
    override val plugin: Plugin,
    override val function: KFunction<*>,
    override val annotation: WebRegexRouting
): GLaDOSSubscription<WebRegexRouting, WebRegexRoutingEvent>(), RoutingHandler<WebRegexRoutingEvent> {
    override val priority: Priority
        get() = annotation.priority

    private val domain = annotation.domain.ifBlank { null }
    private val path = "/${annotation.path.removePrefix("/").removeSuffix("/").trim()}"
    private val regexPath = "^$path$".toRegex(annotation.regexOptions.toSet())
    
    override fun createEvent(context: PipelineContext<*, ApplicationCall>): WebRegexRoutingEvent? {
        val method = context.call.request.httpMethod.glados
        if (method !in annotation.methods && method != HttpMethod.OPTIONS) {
            return null
        }

        if (domain != null && domain != context.call.request.effectiveHost) {
            return null
        }

        val requestPath = context.call.request.path()
        if (!regexPath.matches(requestPath)) {
            return null
        }
        
        val matchResult = regexPath.matchEntire(context.call.request.path())
        
        return WebRegexRoutingEvent(this, context, matchResult)
    }
}

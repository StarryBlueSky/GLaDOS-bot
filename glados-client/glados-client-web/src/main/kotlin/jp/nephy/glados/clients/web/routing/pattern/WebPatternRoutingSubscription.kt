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

package jp.nephy.glados.clients.web.routing.pattern

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

private val fragmentPattern = "^\\{(.+)}$".toRegex()

/**
 * WebPatternRoutingSubscription.
 */
data class WebPatternRoutingSubscription(
    override val plugin: Plugin,
    override val function: KFunction<*>,
    override val annotation: WebPatternRouting
): GLaDOSSubscription<WebPatternRouting, WebPatternRoutingEvent>(), RoutingHandler<WebPatternRoutingEvent> {
    override val priority: Priority
        get() = annotation.priority

    private val domain = annotation.domain.ifBlank { null }
    private val path = "/${annotation.path.removePrefix("/").removeSuffix("/").trim()}"
    
    private val patterns = path.split("/").mapIndexedNotNull { i, it ->
        (fragmentPattern.matchEntire(it)?.groupValues?.get(1) ?: return@mapIndexedNotNull null) to i
    }.toMap()
    
    override fun createEvent(context: PipelineContext<*, ApplicationCall>): WebPatternRoutingEvent? {
        val method = context.call.request.httpMethod.glados
        if (method !in annotation.methods && method != HttpMethod.OPTIONS) {
            return null
        }

        if (domain != null && domain != context.call.request.effectiveHost) {
            return null
        }

        val requestPath = context.call.request.path()

        val (expectPaths, actualPaths) = path.split("/") to requestPath.split("/")
        if (expectPaths.size != actualPaths.size) {
            return null
        }

        for ((expected, actual) in expectPaths.zip(actualPaths)) {
            if (!fragmentPattern.matches(expected) && expected != actual) {
                return null
            }
        }
        
        val currentFragments = context.call.request.path().split("/")

        val fragments = patterns.map { (name, index) ->
            name to currentFragments[index]
        }.toMap()
        
        return WebPatternRoutingEvent(this, context, fragments)
    }
}

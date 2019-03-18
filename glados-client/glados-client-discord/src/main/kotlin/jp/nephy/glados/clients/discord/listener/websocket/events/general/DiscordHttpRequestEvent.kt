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

package jp.nephy.glados.clients.discord.listener.websocket.events.general

import jp.nephy.glados.clients.discord.listener.websocket.DiscordWebsocketEventSubscription
import jp.nephy.glados.clients.discord.listener.websocket.events.DiscordWebsocketEventBase
import net.dv8tion.jda.api.events.http.HttpRequestEvent
import net.dv8tion.jda.api.requests.Request
import net.dv8tion.jda.api.requests.Response
import net.dv8tion.jda.api.requests.RestAction
import net.dv8tion.jda.internal.requests.Route
import okhttp3.Headers
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject

data class DiscordHttpRequestEvent(
    override val subscription: DiscordWebsocketEventSubscription,
    override val jdaEvent: HttpRequestEvent
): DiscordWebsocketEventBase<HttpRequestEvent>

val DiscordHttpRequestEvent.request: Request<*>
    get() = jdaEvent.request

val DiscordHttpRequestEvent.requestBody: RequestBody
    get() = jdaEvent.requestBody

val DiscordHttpRequestEvent.requestBodyRaw: Any
    get() = jdaEvent.requestBodyRaw

val DiscordHttpRequestEvent.requestHeaders: Headers
    get() = jdaEvent.requestHeaders

val DiscordHttpRequestEvent.requestRaw: okhttp3.Request
    get() = jdaEvent.requestRaw

val DiscordHttpRequestEvent.response: Response
    get() = jdaEvent.response

val DiscordHttpRequestEvent.responseBody: ResponseBody
    get() = jdaEvent.responseBody

val DiscordHttpRequestEvent.responseBodyAsArray: JSONArray
    get() = jdaEvent.responseBodyAsArray

val DiscordHttpRequestEvent.responseBodyAsObject: JSONObject
    get() = jdaEvent.responseBodyAsObject

val DiscordHttpRequestEvent.responseHeaders: Headers
    get() = jdaEvent.responseHeaders

val DiscordHttpRequestEvent.responseRaw: okhttp3.Response
    get() = jdaEvent.responseRaw

val DiscordHttpRequestEvent.cfRays: Set<String>
    get() = jdaEvent.cfRays

val DiscordHttpRequestEvent.restAction: RestAction<*>
    get() = jdaEvent.restAction

val DiscordHttpRequestEvent.route: Route.CompiledRoute
    get() = jdaEvent.route

val DiscordHttpRequestEvent.isRateLimit: Boolean
    get() = jdaEvent.isRateLimit

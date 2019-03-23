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

import jp.nephy.glados.api.EventModel
import jp.nephy.glados.clients.web.cookie.WebCookieSetupEvent
import jp.nephy.glados.clients.web.error.WebErrorEvent
import jp.nephy.glados.clients.web.routing.normal.WebRoutingEvent
import jp.nephy.glados.clients.web.routing.pattern.WebPatternRoutingEvent
import jp.nephy.glados.clients.web.routing.regex.WebRegexRoutingEvent

/**
 * WebEventModel.
 */
interface WebEventModel: EventModel {
    /**
     * Called when normal path routing.
     */
    suspend fun onAccess(event: WebRoutingEvent) {}

    /**
     * Called when pattern path routing.
     */
    suspend fun onPatternAccess(event: WebPatternRoutingEvent) {}

    /**
     * Called when regex path routing.
     */
    suspend fun onRegexAccess(event: WebRegexRoutingEvent) {}
    
    /**
     * Called when server errors.
     */
    suspend fun onError(event: WebErrorEvent) {}

    /**
     * Called when cookie setup.
     */
    suspend fun onCookieSetup(event: WebCookieSetupEvent) {}
}

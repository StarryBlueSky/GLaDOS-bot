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

package jp.nephy.glados.clients.web.features

import io.ktor.application.call
import io.ktor.features.origin
import io.ktor.http.ContentType
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.get
import jp.nephy.glados.api.GLaDOS
import jp.nephy.glados.api.Priority
import jp.nephy.glados.api.config
import jp.nephy.glados.clients.subscriptions
import jp.nephy.glados.clients.web.PathType
import jp.nephy.glados.clients.web.config.web
import jp.nephy.glados.clients.web.extensions.effectiveHost
import jp.nephy.glados.clients.web.routing.WebRoutingSubscriptionClient

internal fun Route.sitemapXml() {
    get("/sitemap.xml") {
        if (!GLaDOS.config.web.enableSitemapXmlHandler) {
            return@get
        }
        
        call.respondText(ContentType.Text.Xml) {
            buildString {
                appendln("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
                appendln("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">")
                
                WebRoutingSubscriptionClient.subscriptions.filter { 
                    (it.domain == null || it.domain == call.request.effectiveHost) && !it.banRobots && it.pathType == PathType.Normal 
                }.forEach { page ->
                    appendln("    <url>")
                    appendln("        <loc>${call.request.origin.scheme}://${call.request.effectiveHost}${page.path}</loc>")
                    appendln("        <changefreq>${page.changeFrequency.key}</changefreq>")
                    appendln("        <priority>${page.priority.sitemapPriority}</priority>")
                    appendln("    </url>")
                }
                
                appendln("</urlset>")
            }
        }
    }
}

private val Priority.sitemapPriority: Float
    get() = when (this) {
        Priority.Highest -> 1.0f
        Priority.Higher -> 0.9f
        Priority.High -> 0.7f
        Priority.Normal -> 0.5f
        Priority.Low -> 0.3f
        Priority.Lower -> 0.2f
        Priority.Lowest -> 0.1f
    }

internal fun Route.robotsTxt() {
    get("/robots.txt") {
        if (!GLaDOS.config.web.enableRobotsTxtHandler) {
            return@get
        }
        
        call.respondText(ContentType.Text.Plain) {
            buildString {
                WebRoutingSubscriptionClient.subscriptions.filter { 
                    (it.domain == null || it.domain == call.request.effectiveHost) && it.banRobots && it.pathType == PathType.Normal 
                }.forEach {
                    appendln("User-agent: *")
                    appendln("Disallow: ${it.path}")
                }
                
                appendln("User-agent: *")
                appendln("Sitemap: /sitemap.xml")
            }
        }
    }
}

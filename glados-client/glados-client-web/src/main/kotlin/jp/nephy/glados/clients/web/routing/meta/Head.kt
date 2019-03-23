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

package jp.nephy.glados.clients.web.routing.meta

import kotlinx.html.*
import kotlinx.io.charsets.Charset
import kotlinx.io.charsets.name

@HtmlTagMarker
fun HEAD.charset(charset: Charset) {
    meta(charset = charset.name.toLowerCase())
}

@HtmlTagMarker
fun HEAD.author(name: String) {
    meta(name = "author", content = name)
}

@HtmlTagMarker
fun HEAD.description(content: String) {
    meta(name = "description", content = content)
}

@HtmlTagMarker
fun HEAD.favicon(uri: String) {
    link(rel = "icon", href = uri)
}

@HtmlTagMarker
fun HEAD.canonicalUrl(url: String) {
    link(rel = "canonical", href = url)
}

@HtmlTagMarker
fun HEAD.viewport(content: String) {
    meta(name = "viewport", content = content)
}

@HtmlTagMarker
fun HEAD.robots(vararg policies: RobotsPolicy) {
    meta(name = "robots", content = policies.joinToString(",") { it.name.toLowerCase() })
}

enum class RobotsPolicy {
    Index, NoIndex, Follow, NoFollow, None, NoODP, NoArchive, NoSnippet, NoImageIndex, NoCache
}

@HtmlTagMarker
fun HEAD.disableFormatDetection(vararg formats: DetectionFormat) {
    meta(name = "format-detection", content = formats.joinToString(",") { "${it.name.toLowerCase()}=no" })
}

enum class DetectionFormat {
    Telephone, Email, Address
}

@HtmlTagMarker
fun HEAD.appleTouchIcon(uri: String, sizes: String? = null) {
    link(rel = "apple-touch-icon", href = uri) {
        attributes["sizes"] = sizes ?: return@link
    }
}

@HtmlTagMarker
fun HEAD.appleTouchStartupImage(uri: String) {
    link(rel = "apple-touch-startup-image", href = uri)
}

@HtmlTagMarker
fun HEAD.appleMobileWebAppTitle(title: String) {
    meta(name = "apple-mobile-web-app-title", content = title)
}

@HtmlTagMarker
fun HEAD.appleMobileWebAppCapable(capable: Boolean) {
    meta(name = "apple-mobile-web-app-capable", content = if (capable) "yes" else "no")
}

@HtmlTagMarker
fun HEAD.appleMobileWebAppStatusBarStyle(content: String) {
    meta(name = "apple-mobile-web-app-status-bar-style", content = content)
}

@HtmlTagMarker
fun HEAD.themeColor(content: String) {
    meta(name = "theme-color", content = content)
}

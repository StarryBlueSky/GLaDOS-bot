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

package jp.nephy.glados.clients.web.routing.layout

import jp.nephy.glados.api.GLaDOS
import jp.nephy.glados.api.config
import jp.nephy.glados.api.resourceFile
import jp.nephy.glados.clients.web.config.web
import kotlinx.html.*
import java.nio.charset.Charset

private val minifyRegex = "(?:\\s{2,}|\\n)".toRegex()
private fun staticFile(path: String, minified: Boolean, charset: Charset): String {
    val paths = path.removePrefix("/").removeSuffix("/").split("/").toTypedArray()

    return runCatching {
        GLaDOS.resourceFile(GLaDOS.config.web.staticDirectory, *paths).readText(charset)
    }.map {
        if (minified) {
            it.replace(minifyRegex, "")
        } else {
            it
        }
    }.getOrNull().orEmpty()
}

/**
 * Inserts CSS content inline-ly.
 */
fun FlowOrMetaDataContent.inlineCSS(path: String, minified: Boolean = true, charset: Charset = Charsets.UTF_8) {
    style {
        unsafe {
            +staticFile(path, minified, charset)
        }
    }
}

/**
 * Inserts JavaScript content inline-ly.
 */
fun FlowOrPhrasingOrMetaDataContent.inlineJS(path: String, async: Boolean = false, defer: Boolean = false, type: String? = null, minified: Boolean = true, charset: Charset = Charsets.UTF_8) {
    script(type = type) {
        this.async = async
        this.defer = defer
        
        unsafe {
            +staticFile(path, minified, charset)
        }
    }
}

/**
 * Inserts HTML content inline-ly.
 */
fun FlowContent.inlineHTML(path: String, minified: Boolean = true, charset: Charset = Charsets.UTF_8) {
    consumer.onTagContentUnsafe {
        +staticFile(path, minified, charset)
    }
}

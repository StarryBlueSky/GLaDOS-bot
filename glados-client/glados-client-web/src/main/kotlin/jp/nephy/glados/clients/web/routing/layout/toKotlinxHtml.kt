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

package jp.nephy.glados.clients.web.routing.layout

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode

/**
 * Converts html syntax to kotlinx.html syntax.
 */
fun String.toKotlinxHtmlSyntax(): String {
    return Jsoup.parse(this).body().childNodes().joinToString("\n") { dump(it) }
}

private fun dump(node: Node): String {
    return buildString {
        if (node is Element) {
            append(node.tagName())
            if (node.attributes().size() > 0) {
                append("(${node.attributes().joinToString(", ") {
                    "${if (it.key == "class") {
                        "classes"
                    } else {
                        it.key
                    }} = \"${it.value}\""
                }})")
            } else if (node.childNodeSize() == 0) {
                append("()")
            }

            if (node.childNodeSize() > 0) {
                appendln(" {")
                node.childNodes().filter { it is Element || (it is TextNode && !it.isBlank) }.forEach {
                    appendln("    ${dump(it)}")
                }
                append("}")
            }
        } else if (node is TextNode) {
            append("+ \"${node.text()}\"")
        }
    }
}

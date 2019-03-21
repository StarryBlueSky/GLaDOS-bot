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

package jp.nephy.glados.clients.web.config

import jp.nephy.jsonkt.*
import jp.nephy.jsonkt.delegation.*

data class WebConfig(override val json: JsonObject): JsonModel {
    val host by string { "127.0.0.1" }
    val port by int { 8080 }
    
    val logging by modelOrDefault<Logging>()

    val staticDirectory: String by string("static_directory") { "static" }
    val defaultFiles: List<String> by stringList("default_files") { listOf("index.html") }
    
    val enableHeadRequestHandler: Boolean by boolean("enable_head_request_handler") { true }
    val enableOptionsRequestHandler: Boolean by boolean("enable_options_request_handler") { true }
    
    val enableRobotsTxtHandler: Boolean by boolean("enable_robots_txt_handler") { true }
    val enableSitemapXmlHandler: Boolean by boolean("enable_sitemap_xml_handler") { true }
    
    data class Logging(override val json: JsonObject): JsonModel {
        val ignoreIpAddresses by lambdaList("ignore_ip_addresses") { it.string.toRegex() }
        val ignoreUserAgents by lambdaList("ignore_user_agents") { it.string.toRegex() }
    }
}

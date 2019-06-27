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

package jp.nephy.glados.core

import ch.qos.logback.classic.Level
import jp.nephy.glados.api.*
import jp.nephy.jsonkt.*
import jp.nephy.jsonkt.delegation.*
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

internal data class ConfigJsonImpl(override val json: JsonObject): ConfigJson {
    companion object {
        private val productionConfigJsonPath: Path = Paths.get("config.prod.json")
        private val developmentConfigJsonPath: Path = Paths.get("config.dev.json")
        
        fun load(): ConfigJson {
            val logger = Logger.of("GLaDOS.Config.ConfigJson")
            
            return if (GLaDOS.isDevelopmentMode) {
                if (!Files.exists(developmentConfigJsonPath)) {
                    logger.info { "$developmentConfigJsonPath は存在しません。デフォルトの JSON をコピーします。" }
                    
                    val defaultJson = this::class.java.classLoader.getResource(developmentConfigJsonPath.fileName.toString())!!.readText()
                    developmentConfigJsonPath.toFile().writeText(defaultJson)
                }
                
                logger.info { "Development モードの設定をロードします。" }
                developmentConfigJsonPath.parse<ConfigJsonImpl>()
            } else {
                if (!Files.exists(productionConfigJsonPath)) {
                    logger.info { "$productionConfigJsonPath は存在しません。空の JSON を新たに作成します。" }

                    val defaultJson = this::class.java.classLoader.getResource(productionConfigJsonPath.fileName.toString())!!.readText()
                    productionConfigJsonPath.toFile().writeText(defaultJson)
                }
                
                logger.info { "Production モードの設定をロードします。" }
                productionConfigJsonPath.parse()
            }
        }
        
        fun installFileSystemListener() {
            GLaDOS.fileSystemWatcher.addListener(object: FileSystemEventListener {
                override fun onCreated(path: Path) {
                    handleFileChange(path)
                }

                override fun onModified(path: Path) {
                    handleFileChange(path)
                }
            })
        }

        @Suppress("DEPRECATION")
        private fun handleFileChange(path: Path) {
            if ((!GLaDOS.isDevelopmentMode && Files.isSameFile(path, productionConfigJsonPath)) || (GLaDOS.isDevelopmentMode && Files.isSameFile(path, developmentConfigJsonPath))) {
                GLaDOS.Instance.config = load()
            }
        }
    }

    override val parallelism: Int by int { maxOf(Runtime.getRuntime().availableProcessors() / 2, 1) }

    override val userAgent: String by string("user_agent") { "GLaDOS-bot (+https://github.com/NephyProject/GLaDOS-bot)" }
    
    override val paths: ConfigJson.Paths by modelOrDefault<PathsImpl>()
    
    data class PathsImpl(override val json: JsonObject): ConfigJson.Paths {
        override val tmp: Path by lambda(default = { Paths.get("tmp") }) { Paths.get(it.string) }
        override val resources: Path by lambda(default = { Paths.get("resources") }) { Paths.get(it.string) }
        override val plugins: Path by lambda(default = { Paths.get("plugins") }) { Paths.get(it.string) }
        override val libs: Path by lambda(default = { Paths.get("libs") }) { Paths.get(it.string) }
    }
    
    override val logging: ConfigJson.Logging by modelOrDefault<LoggingImpl>()
            
    data class LoggingImpl(override val json: JsonObject): ConfigJson.Logging {
        override val level: Level by lambda("level", { Level.INFO }) { Level.toLevel(it.stringOrNull, Level.INFO) }

        override val levelForSlack: Level by lambda("level_for_slack", { Level.INFO }) { Level.toLevel(it.stringOrNull, Level.INFO) }

        override val slackWebhookUrl: String? by nullableString("slack_webhook_url")
    }
}

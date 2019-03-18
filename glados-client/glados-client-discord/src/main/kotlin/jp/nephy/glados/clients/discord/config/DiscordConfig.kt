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

package jp.nephy.glados.clients.discord.config

import jp.nephy.jsonkt.*
import jp.nephy.jsonkt.delegation.*

/**
 * Represents Discord config json model defined in config.json.
 * 
 * ```json
 * {
 *     "discord": {
 *         ...
 *     },
 *     ...
 * }
 * ```
 */
data class DiscordConfig(override val json: JsonObject): JsonModel {
    val token: String by string
    val ownerId: Long? by nullableLong("owner_id")
    val prefix: String by string { "!" }
    
    val defaultActivity: String by string("default_activity") { "Initializing..." }
    
    val enableJDAShutdownHook: Boolean by boolean("enable_jda_shutdown_hook") { true }

    val guildMap: Map<String, GuildConfig> by lambda("guilds") { 
        it.jsonObject.map { guild ->
            guild.key to guild.value.jsonObject.parse<GuildConfig>()
        }.toMap() 
    }
    
    val guilds: List<GuildConfig> by lazy { guildMap.values.toList() }
}

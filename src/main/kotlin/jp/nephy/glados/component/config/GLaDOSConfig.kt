package jp.nephy.glados.component.config

import net.dv8tion.jda.core.entities.Guild


data class GLaDOSConfig(
        val token: String,

        val guilds: List<GuildConfig>
) {
    init {
        if (guilds.isEmpty()) {
            throw IllegalStateException("Configにサーバ設定がありません.")
        } else if (guilds.count { it.isMain } > 1) {
            throw IllegalStateException("メインサーバは1つのみ設定できます.")
        }
    }

    private val guildMap = guilds.map { it.id to it }.toMap().toMutableMap()
    fun getGuildConfig(guild: Guild): GuildConfig {
        return guildMap.getOrPut(guild.idLong) { GuildConfig(guild.idLong) }
    }
}

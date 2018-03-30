package jp.nephy.glados.component.config

import jp.nephy.glados.component.config.additional.SteamGameRoleConfig

data class GuildOption(
        val useCommand: Boolean = false,

        val useAutoPlaylist: Boolean = false,
        val useFindVideoURL: Boolean = false,
        val useNiconicoDict: Boolean = false,
        val disableServerMute: Boolean = false,

        val ignoreUserIds: List<Long> = emptyList(),

        val clientToken: String? = null,
        val steamGameRoles: List<SteamGameRoleConfig> = emptyList()
)

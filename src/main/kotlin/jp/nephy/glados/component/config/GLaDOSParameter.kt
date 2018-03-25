package jp.nephy.glados.component.config

data class GLaDOSParameter(
        val ownerId: Long,
        val primaryCommandPrefix: String,
        val secondaryCommandPrefix: String,

        val defaultPlayerVolume: Int,
        val nowPlayingUpdateMs: Long
)

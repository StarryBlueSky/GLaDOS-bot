package jp.nephy.glados.component.config

data class GuildConfig(
        val id: Long,
        val isMain: Boolean = false,

        val role: RoleConfig = RoleConfig(),
        val voiceChannel: VoiceChannelConfig = VoiceChannelConfig(),
        val textChannel: TextChannelConfig = TextChannelConfig(),

        val option: GuildOption = GuildOption()
)

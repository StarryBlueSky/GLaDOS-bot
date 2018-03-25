package jp.nephy.glados.component.api.soundcloud.param

import jp.nephy.glados.component.helper.prompt.IPromptEmoji

enum class ChartType(override val emoji: String): IPromptEmoji {
    Top("🌟"), Trending("🔥");

    val internalName: String
        get() = name.toLowerCase()
    override val friendlyName: String
        get() = when (this) {
            Top -> "人気順"
            Trending -> "話題順"
        }
}

package jp.nephy.glados.feature.listener.kaigen.kashiwa

enum class HateEmoji(val emoji: String) {
    Face1("😩"), Face2("😰"), Face3("😟");

    companion object {
        fun fromEmoji(emoji: String): HateEmoji? {
            return values().find { it.emoji == emoji }
        }
    }
}

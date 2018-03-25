package jp.nephy.glados.component.helper.prompt

enum class YesNoEmoji(override val emoji: String): IPromptEmoji {
    Yes("👌"), No("👋");

    override val friendlyName: String
        get() = when (this) {
            Yes -> "はい"
            No -> "いいえ"
        }
}

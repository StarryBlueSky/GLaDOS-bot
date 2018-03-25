package jp.nephy.glados.component.api.niconico.param

import jp.nephy.glados.component.helper.prompt.IPromptEmoji

enum class RankingType(override val emoji: String, override val friendlyName: String, val internalName: String): IPromptEmoji {
    Top("🌟", "総合", "fav"),
    View("📺", "再生", "view"),
    Comment("💬", "コメント", "res"),
    MyList("📂", "マイリスト", "mylist")
}

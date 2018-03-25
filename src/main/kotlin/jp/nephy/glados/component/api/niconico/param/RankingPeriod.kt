package jp.nephy.glados.component.api.niconico.param

import jp.nephy.glados.component.helper.prompt.IPromptEmoji

enum class RankingPeriod(override val emoji: String, override val friendlyName: String): IPromptEmoji {
    Hourly("🕒", "毎時"),
    Daily("⏰", "24時間"),
    Weekly("⏳", "週間"),
    Monthly("📅", "月間"),
    Total("📊", "合計")
}

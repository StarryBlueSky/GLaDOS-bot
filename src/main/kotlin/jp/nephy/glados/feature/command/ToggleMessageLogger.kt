package jp.nephy.glados.feature.command

import com.jagrosh.jdautilities.command.CommandEvent
import jp.nephy.glados.component.helper.Color
import jp.nephy.glados.component.helper.FeatureHelper
import jp.nephy.glados.component.helper.embedMention
import jp.nephy.glados.feature.CommandFeature

class ToggleMessageLogger: CommandFeature() {
    init {
        name = "msglogger"
        help = "メッセージロガーの有効無効を切り替えます。"

        isAdminCommand = true
    }

    override fun executeCommand(event: CommandEvent) {
        FeatureHelper.messageLoggerEnabled = ! FeatureHelper.messageLoggerEnabled
        event.embedMention {
            title("メッセージロガー切り替え")
            description {
                if (FeatureHelper.messageLoggerEnabled) {
                    "メッセージロガーを有効にしました。"
                } else {
                    "メッセージロガーを無効にしました。"
                }
            }
            color(Color.Good)
            timestamp()
        }.queue()
    }
}

package jp.nephy.glados.feature.command

import com.jagrosh.jdautilities.command.CommandEvent
import jp.nephy.glados.feature.CommandFeature
import jp.nephy.glados.feature.listener.kaigen.kashiwa.KashiwaListener

class ChangeIHSKCommand: CommandFeature() {
    init {
        name = "IHSKChange"
        help = "IHSKチャンネルに登録するときのコマンドを変更します"

        isAdminCommand = true
        arguments = "<新コマンド>"
    }

    override fun executeCommand(event: CommandEvent) {
        KashiwaListener.hateCommandString = event.args
    }
}

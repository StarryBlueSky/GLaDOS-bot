package jp.nephy.glados.feature.command

import com.jagrosh.jdautilities.command.CommandEvent
import jp.nephy.glados.GLaDOS
import jp.nephy.glados.component.api.twitter.kashiwa.UserStreamListener
import jp.nephy.glados.feature.CommandFeature

class ChangeIHSKCommand(bot: GLaDOS): CommandFeature(bot) {
    init {
        name = "IHSKChange"
        help = "IHSKチャンネルに登録するときのコマンドを変更します"

        isAdminCommand = true
        arguments = "<新コマンド>"
    }

    override fun executeCommand(event: CommandEvent) {
        UserStreamListener.hateCommandString = event.args
    }

}
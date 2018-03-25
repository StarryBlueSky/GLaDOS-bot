package jp.nephy.glados.feature.command

import com.jagrosh.jdautilities.command.CommandEvent
import jp.nephy.glados.GLaDOS
import jp.nephy.glados.component.helper.Color
import jp.nephy.glados.component.helper.EmbedBuilder
import jp.nephy.glados.feature.CommandFeature
import net.dv8tion.jda.core.entities.Icon
import java.net.URL


class Icon(bot: GLaDOS): CommandFeature(bot) {
    init {
        name = "icon"
        help = "GLaDOSのアイコンを変更します。"
        guildOnly = false

        isAdminCommand = true
        arguments = "<画像URL>"
    }

    override fun executeCommand(event: CommandEvent) {
        event.reply(
                EmbedBuilder.build {
                    title("アイコンを変更しています...")
                    color(Color.Plain)
                }
        ) {
            val bytes = URL(event.args).readBytes()
            event.selfUser.manager.setAvatar(Icon.from(bytes)).queue { _ ->
                it.editMessage(EmbedBuilder.build {
                    title("アイコンを変更しました！")
                    color(Color.Good)
                }).queue()
            }
        }
    }
}

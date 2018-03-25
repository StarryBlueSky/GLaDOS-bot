package jp.nephy.glados.feature.command

import com.jagrosh.jdautilities.command.CommandEvent
import jp.nephy.glados.GLaDOS
import jp.nephy.glados.component.helper.Color
import jp.nephy.glados.component.helper.EmbedBuilder
import jp.nephy.glados.feature.CommandFeature
import javax.script.ScriptEngineManager
import kotlin.system.measureTimeMillis


class Eval(bot: GLaDOS): CommandFeature(bot) {
    init {
        name = "eval"
        help = "JavaScriptを実行します。"
        guildOnly = false

        isAdminCommand = true
        arguments = "<JavaScriptコード>"
    }

    override fun executeCommand(event: CommandEvent) {
        event.reply(
                EmbedBuilder.build {
                    title("実行中...")
                    description { "> `${event.args}`" }
                    color(Color.Plain)
                }
        ) {
            bot.logger.info { "eval: ${event.args}" }
            val engine = ScriptEngineManager().getEngineByName("Nashorn").apply {
                put("event", event)
                put("jda", event.jda)
                put("guild", event.guild)
                put("channel", event.channel)
                put("member", event.member)
            }

            it.editMessage(EmbedBuilder.build {
                title("実行結果")
                try {
                    val time = measureTimeMillis {
                        descriptionBuilder {
                            appendln("> `${event.args}`")
                            append(engine.eval(event.args))
                        }
                    }
                    footer("実行時間: ${time}ms")

                    color(Color.Good)
                } catch (e: Exception) {
                    descriptionBuilder {
                        appendln("失敗しました:")
                        append(e.localizedMessage)
                    }

                    color(Color.Bad)
                }
            }).queue()
        }
    }
}

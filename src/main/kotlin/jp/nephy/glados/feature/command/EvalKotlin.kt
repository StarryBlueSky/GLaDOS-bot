package jp.nephy.glados.feature.command

import com.jagrosh.jdautilities.command.CommandEvent
import jp.nephy.glados.component.helper.Color
import jp.nephy.glados.component.helper.EmbedBuilder
import jp.nephy.glados.feature.CommandFeature
import jp.nephy.glados.logger
import javax.script.ScriptEngineManager
import kotlin.system.measureTimeMillis


class EvalKotlin: CommandFeature() {
    init {
        name = "evalk"
        help = "Kotlin Scriptを実行します。"
        guildOnly = false

        isAdminCommand = true
        arguments = "<Kotlinコード>"
    }

    override fun executeCommand(event: CommandEvent) {
        event.reply(
                EmbedBuilder.build {
                    title("実行中...")
                    description { "> `${event.args}`" }
                    color(Color.Plain)
                }
        ) {
            logger.info { "eval: ${event.args}" }
            val engine = ScriptEngineManager().getEngineByName("kotlin").apply {
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

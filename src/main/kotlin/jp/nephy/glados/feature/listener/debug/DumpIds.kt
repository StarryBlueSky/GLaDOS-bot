package jp.nephy.glados.feature.listener.debug

import jp.nephy.glados.component.helper.fullNameWithoutGuild
import jp.nephy.glados.component.helper.tmpFile
import jp.nephy.glados.feature.ListenerFeature
import jp.nephy.glados.logger
import net.dv8tion.jda.core.events.ReadyEvent


class DumpIds: ListenerFeature() {
    override fun onReady(event: ReadyEvent) {
        event.jda.guilds.forEach {
            val file = tmpFile("ids_${it.id}.txt")
            file.writeText(
                    buildString {
                        appendln(it.name)

                        appendln("  ロールID一覧")
                        it.roles.forEach {
                            appendln("    ${it.name}")
                            appendln("      ${it.id}")
                        }

                        appendln("\n  テキストチャンネルID一覧")
                        it.textChannels.forEach {
                            appendln("    ${it.name}")
                            appendln("      ${it.id}")
                        }

                        appendln("\n  ボイスチャンネルID一覧")
                        it.voiceChannels.forEach {
                            appendln("    ${it.name}")
                            appendln("      ${it.id}")
                        }

                        appendln("\n  カテゴリーID一覧")
                        it.categories.forEach {
                            appendln("    ${it.name}")
                            appendln("      ${it.id}")
                        }

                        appendln("\n  絵文字ID一覧")
                        it.emotes.forEach {
                            appendln("    ${it.name}")
                            appendln("      ${it.id}")
                        }

                        appendln("\n  メンバーID一覧")
                        it.members.forEach {
                            appendln("    ${it.fullNameWithoutGuild}")
                            appendln("      ${it.user.id}")
                        }
                    }
            )

            logger.info { "サーバ ${it.name}のID一覧を ${file.toPath().toAbsolutePath()} に書き出しました." }
        }
    }
}

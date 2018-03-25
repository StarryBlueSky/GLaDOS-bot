package jp.nephy.glados.feature.listener

import jp.nephy.glados.GLaDOS
import jp.nephy.glados.feature.ListenerFeature
import net.dv8tion.jda.core.entities.Game
import net.dv8tion.jda.core.events.ReadyEvent
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread


class UpdateTestChamber(bot: GLaDOS): ListenerFeature(bot) {
    override fun onReady(event: ReadyEvent) {
        thread(name = "Update Test Chamber") {
            val rnd = Random()

            while (true) {
                val message = "Test Chamber ${rnd.nextInt(21) + 1}"
                val game = listOf(
                        Game.watching(message),
                        Game.listening(message),
                        Game.streaming(message, "https://www.twitch.tv/slashnephy")
                )[rnd.nextInt(3)]
                event.jda.presence.game = game

                TimeUnit.SECONDS.sleep(30)
            }
        }
    }
}

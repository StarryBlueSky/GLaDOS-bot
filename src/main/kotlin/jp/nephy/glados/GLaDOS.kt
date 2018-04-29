package jp.nephy.glados

import com.jagrosh.jdautilities.command.CommandClientBuilder
import com.jagrosh.jdautilities.commons.waiter.EventWaiter
import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory
import jp.nephy.glados.component.MessageCacheManager
import jp.nephy.glados.component.api.ApiClient
import jp.nephy.glados.component.audio.music.PlayerManager
import jp.nephy.glados.component.config.GLaDOSConfig
import jp.nephy.glados.component.config.GLaDOSParameter
import jp.nephy.glados.component.config.SecretConfig
import jp.nephy.glados.component.helper.Color
import jp.nephy.glados.component.helper.embedMention
import jp.nephy.glados.component.helper.enumuratePackage
import jp.nephy.glados.feature.CommandFeature
import jp.nephy.glados.feature.ListenerFeature
import mu.KotlinLogging
import net.dv8tion.jda.core.AccountType
import net.dv8tion.jda.core.JDABuilder
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.measureTimeMillis


class GLaDOS(val config: GLaDOSConfig, val parameter: GLaDOSParameter, val secret: SecretConfig) {
    companion object {
        const val version = "0.8.0"
        var debug = false

        fun getTmpFile(first: String, vararg more: String): File {
            val tmpDir = Paths.get(if (debug) {
                "tmp_debug"
            } else {
                "tmp"
            })
            if (! Files.exists(tmpDir)) {
                Files.createDirectory(tmpDir)
            }

            if (more.isNotEmpty()) {
                Files.createDirectories(Paths.get(tmpDir.toString(), first, *more.dropLast(1).toTypedArray()))
            }

            return Paths.get(tmpDir.toString(), first, *more).toFile()
        }
    }

    val logger = KotlinLogging.logger("GLaDOS")
    val apiClient = ApiClient(this)
    val playerManager = PlayerManager(this)
    val messageCacheManager = MessageCacheManager()

    val eventWaiter = EventWaiter()
    private val client = CommandClientBuilder()
            .setPrefix(parameter.primaryCommandPrefix)
            .setAlternativePrefix(parameter.secondaryCommandPrefix)
            .setOwnerId(parameter.ownerId.toString())
            .setEmojis("\uD83D\uDE03", "\uD83D\uDE2E", "\uD83D\uDE26")
            .setHelpConsumer {
                it.embedMention {
                    title("利用可能なコマンドの一覧です")
                    color(Color.Good)
                    it.client.commands.filterNot { it.isHidden }.forEach {
                        field("${it.name} ${it.arguments ?: ""}") { it.help }
                    }
                }.queue()
            }
            .build()!!

    val jda = JDABuilder(AccountType.BOT)
            .setToken(config.token)
            .setAudioSendFactory(NativeAudioSendFactory())
            .addEventListener(eventWaiter)
            .addEventListener(client)
            .apply {
                val featuresLoadMs = measureTimeMillis {
                    enumuratePackage<ListenerFeature>("jp.nephy.glados.feature.listener")
                            .sortedBy { it.canonicalName }
                            .map { it.getConstructor(GLaDOS::class.java).newInstance(this@GLaDOS) }
                            .forEach {
                                addEventListener(it)
                                logger.debug { "Listener: ${it.javaClass.canonicalName} がロードされました." }
                            }
                    enumuratePackage<CommandFeature>("jp.nephy.glados.feature.command")
                            .sortedBy { it.simpleName }
                            .map { it.getConstructor(GLaDOS::class.java).newInstance(this@GLaDOS) }
                            .forEach {
                                client.addCommand(it)
                                logger.debug { "Command: ${it.aliases.plus(it.name).joinToString(", ")} が追加されました." }
                            }
                }
                logger.info { "${featuresLoadMs}ms でFeatureのロードを完了しました." }
            }.buildAsync()!!
}

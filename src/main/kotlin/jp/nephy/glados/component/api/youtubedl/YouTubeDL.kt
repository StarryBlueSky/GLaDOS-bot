package jp.nephy.glados.component.api.youtubedl

import jp.nephy.glados.component.api.youtubedl.model.YouTubeDLInfo
import jp.nephy.jsonkt.JsonKt
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.file.Path


class YouTubeDL(private val videoUrl: String) {
    private var cachedVideoStream: InputStream? = null
    private var cachedVideoInfo: YouTubeDLInfo? = null

    private fun getResultStream(vararg args: String): InputStream {
        val processBuilder = ProcessBuilder("youtube-dl", *args, videoUrl)
        val process = processBuilder.start()
        process.waitFor()
        return process.inputStream
    }
    private fun getResultAsString(vararg args: String): String {
        return InputStreamReader(getResultStream(*args)).use { it.readText() }.trim()
    }

    fun download(path: Path): Boolean {
        return try {
            getResultStream("-o", path.toString())
            true
        } catch (e: Exception) {
            println(e.localizedMessage)
            false
        }
    }

    val videoStream: InputStream?
        get() = cachedVideoStream ?: try {
            getResultStream("-o", "-").also {
                cachedVideoStream = it
            }
        } catch (e: Exception) {
            null
        }
    val info: YouTubeDLInfo?
        get() = cachedVideoInfo ?: try {
            JsonKt.parse<YouTubeDLInfo>(getResultAsString("-j")).also {
                cachedVideoInfo = it
            }
        } catch (e: Exception) {
            null
        }
}

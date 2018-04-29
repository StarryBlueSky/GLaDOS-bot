package jp.nephy.glados.component.api

import com.lukaspradel.steamapi.webapi.client.SteamWebApiClient
import jp.nephy.glados.GLaDOS
import jp.nephy.glados.component.api.niconico.NiconicoClient
import jp.nephy.glados.component.api.soundcloud.SoundCloudClient
import jp.nephy.glados.component.api.youtube.YouTubeClient

class ApiClient(bot: GLaDOS) {
    val soundCloud = SoundCloudClient(bot.secret.soundCloudClientId)
    val twitter = bot.secret.twiter
    val niconico = NiconicoClient()
    val youtube = YouTubeClient(bot.secret.googleApiKey)
    val steam = SteamWebApiClient.SteamWebApiClientBuilder(bot.secret.steamApiKey).build()!!
}

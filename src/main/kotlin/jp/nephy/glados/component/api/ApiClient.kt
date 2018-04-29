package jp.nephy.glados.component.api

import com.lukaspradel.steamapi.webapi.client.SteamWebApiClient
import jp.nephy.glados.GLaDOS
import jp.nephy.glados.component.api.niconico.NiconicoClient
import jp.nephy.glados.component.api.soundcloud.SoundCloudClient
import jp.nephy.glados.component.api.youtube.YouTubeClient

class ApiClient {
    private val secret by lazy { GLaDOS.instance.secret }

    val soundCloud by lazy { SoundCloudClient(secret.soundCloudClientId) }
    val twitter by lazy { secret.twiter }
    val niconico by lazy { NiconicoClient() }
    val youtube by lazy { YouTubeClient(secret.googleApiKey) }
    val steam by lazy { SteamWebApiClient.SteamWebApiClientBuilder(secret.steamApiKey).build()!! }
}

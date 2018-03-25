package jp.nephy.glados.component.api

import com.lukaspradel.steamapi.webapi.client.SteamWebApiClient
import jp.nephy.glados.GLaDOS
import jp.nephy.glados.component.api.niconico.NiconicoClient
import jp.nephy.glados.component.api.soundcloud.SoundCloudClient
import jp.nephy.glados.component.api.youtube.YouTubeClient
import jp.nephy.penicillin.Client
import jp.nephy.penicillin.credential.AccessToken
import jp.nephy.penicillin.credential.AccessTokenSecret
import jp.nephy.penicillin.credential.OfficialClient

class ApiClient(bot: GLaDOS) {
    val soundCloud = SoundCloudClient(bot.secret.soundCloudClientId)
    val twitter = Client.builder()
            .officialClient(
                    OfficialClient.Twitter_for_Mac,
                    AccessToken(""),
                    AccessTokenSecret("")
            ).build()
    val niconico = NiconicoClient()
    val youtube = YouTubeClient(bot.secret.googleApiKey)
    val steam = SteamWebApiClient.SteamWebApiClientBuilder(bot.secret.steamApiKey).build()!!
}

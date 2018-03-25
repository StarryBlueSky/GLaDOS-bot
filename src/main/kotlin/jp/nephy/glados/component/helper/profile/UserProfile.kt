package jp.nephy.glados.component.helper.profile

import com.google.gson.JsonObject
import jp.nephy.jsonkt.*

class UserProfile(override val json: JsonObject): JsonModel {
    val connectedAccounts by json.byModelList<ConnectedAccounts>(key = "connected_accounts")  // [...]
    val mutualGuilds by json.byModelList<MutualGuilds>(key = "mutual_guilds")  // [{"nick":"?","id":"187578406940966912"}, ...]
    val premiumSince by json.byNullableString("premium_since")  // "2017-03-23T11:02:27+00:00"
    val user by json.byModel<User>()  // {...}
}

class ConnectedAccounts(override val json: JsonObject): JsonModel {
    val id by json.byString  // "123775508"
    val name by json.byString  // "slashnephy"
    val type by json.byString  // "twitch"
    val verified by json.byBool  // true
}

class MutualGuilds(override val json: JsonObject): JsonModel {
    val id by json.byString  // "187578406940966912"
    val nick by json.byNullableString  // "?" or null
}

class User(override val json: JsonObject): JsonModel {
    val avatar by json.byString  // "505575f2ba030f7f29d190a2afe25d30"
    val discriminator by json.byString  // "6666"
    val flags by json.byInt  // 4
    val id by json.byString  // "189327611829288960"
    val username by json.byString  // "razlite"
}

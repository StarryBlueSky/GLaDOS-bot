package jp.nephy.glados.model

import com.google.gson.JsonObject
import jp.nephy.jsonkt.*

class MemberModel(override val json: JsonObject): JsonModel {
    val index by json.byInt
    val importFrom by json.byString("import_from")
    val id by json.byString
    val name by json.byString
    val nickname by json.byNullableString
    val effectiveName by json.byString("effective_name")
    val alias by json.byStringList
    val roles by json.byLambdaList { Role.valueOf(string) }
    val isInitialKaigen by json.byBool("is_initial_kaigen")
    val profile by json.byModel<Profile>()
    val skills by json.byModelList<Skill>()
    val accounts by json.byModel<Accounts>()
}

enum class Role(val description: String) {
    InitialKaigen("初期かいげんメンバー"),
    ProjectAdmin("プロジェクト管理者"), Contributor("プロジェクト支援者"), Developer("デベロッパー"), Designer("デザイナー"), BetaTester("ベータテスター"),
    DiscordAdmin("Discord管理者")
}

class Profile(override val json: JsonObject): JsonModel {
    val bio by json.byNullableString
    val birthday by json.byModel<Birthday>()
    val email by json.byStringList
    val iconUrl by json.byString("icon_url")
    val location by json.byNullableString
    val prefecture by json.byString
    val url by json.byString
    val amazonWishlistUrl by json.byNullableString("amazon_wishlist_url")
}

class Birthday(override val json: JsonObject): JsonModel {
    val day by json.byNullableInt
    val month by json.byNullableInt
    val year by json.byNullableInt
}

class Skill(override val json: JsonObject): JsonModel {
    val degree by json.byInt
    val name by json.byString
}

class Accounts(override val json: JsonObject): JsonModel {
    val twitter by json.byModelList<Twitter>()
    val steam by json.byModelList<Steam>()
    val discord by json.byModelList<Discord>()
    val github by json.byModelList<GitHub>()
    val twitch by json.byModelList<Twitch>()
    val instagram by json.byModelList<Instagram>()
    val niconico by json.byModelList<Niconico>()
    val niconicoCommunity by json.byModelList<NiconicoCommunity>(key = "niconico_community")
    val youtube by json.byModelList<YouTube>()
    val flickr by json.byModelList<Flickr>()
    val minecraft by json.byModelList<Minecraft>()
    val ff14 by json.byModelList<FF14>()
    val other by json.byModelList<Other>()
}

abstract class Account(final override val json: JsonObject): JsonModel {
    val private by json.byBool { false }
    val internal by json.byBool { false }
}

interface WithProfileUrl {
    val profileUrl: String
}

class Twitter(json: JsonObject): Account(json), WithProfileUrl {
    val id by json.byLong
    val sn by json.byString
    override val profileUrl: String
        get() = "https://twitter.com/$sn"
}

class Discord(json: JsonObject): Account(json) {
    val tag by json.byString
}

class Steam(json: JsonObject): Account(json), WithProfileUrl {
    val familySharing by json.byLongList("family_sharing")
    val id by json.byLong
    val username by json.byNullableString

    override val profileUrl: String
        get() = if (username != null) {
            "https://steamcommunity.com/id/$username"
        } else {
            "https://steamcommunity.com/profiles/$id"
        }
}

class GitHub(json: JsonObject): Account(json), WithProfileUrl {
    val username by json.byString
    override val profileUrl: String
        get() = "https://github.com/$username"
}

class Minecraft(json: JsonObject): Account(json), WithProfileUrl {
    val id by json.byNullableString
    val uuid by json.byString
    override val profileUrl: String
        get() = "https://ja.namemc.com/profile/$uuid"
}

class Twitch(json: JsonObject): Account(json), WithProfileUrl {
    val id by json.byString
    override val profileUrl: String
        get() = "https://twitch.tv/$id"
}

class Instagram(json: JsonObject): Account(json), WithProfileUrl {
    val id by json.byString
    override val profileUrl: String
        get() = "https://www.instagram.com/$id"
}

class Niconico(json: JsonObject): Account(json), WithProfileUrl {
    val id by json.byLong
    override val profileUrl: String
        get() = "http://www.nicovideo.jp/user/$id"
}

class NiconicoCommunity(json: JsonObject): Account(json), WithProfileUrl {
    val id by json.byString
    override val profileUrl: String
        get() = "http://com.nicovideo.jp/community/$id"
}

class YouTube(json: JsonObject): Account(json), WithProfileUrl {
    val id by json.byString
    override val profileUrl: String
        get() = "https://www.youtube.com/user/$id"
}

class Flickr(json: JsonObject): Account(json), WithProfileUrl {
    val id by json.byString
    val username by json.byString
    override val profileUrl: String
        get() = "https://www.flickr.com/photos/$id"
}

class FF14(json: JsonObject): Account(json), WithProfileUrl {
    val id by json.byLong
    override val profileUrl: String
        get() = "https://jp.finalfantasyxiv.com/lodestone/character/$id"
}

class Other(json: JsonObject): Account(json), WithProfileUrl {
    val id by json.byNullableString
    override val profileUrl by json.byString("profile_url")
    val service by json.byModel<UnknownService>()
    val username by json.byNullableString
}

class UnknownService(override val json: JsonObject): JsonModel {
    val name by json.byString
    val url by json.byString
}

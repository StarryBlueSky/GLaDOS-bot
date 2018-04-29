package jp.nephy.glados.model

import com.google.gson.JsonObject
import jp.nephy.jsonkt.*


class Member(override val json: JsonObject): JsonModel {
    val accounts by json.byModel<Accounts>()
    private val name by json.byString
    private val nickname by json.byNullableString
    val profile by json.byModel<Profile>()

    val effectiveName: String
        get() = if (nickname != null) {
            nickname!!
        } else {
            name
        }
}

class Accounts(override val json: JsonObject): JsonModel {
    val discord by json.byModelList<Discord>()
    val twitter by json.byModelList<Twitter>()
}

class Profile(override val json: JsonObject): JsonModel {
    val birthday by json.byModel<Birthday?>()
}

class Birthday(override val json: JsonObject): JsonModel {
    val day by json.byNullableInt
    val month by json.byNullableInt
}

class Twitter(override val json: JsonObject): JsonModel {
    val id by json.byLong
    val private by json.byBool { false }
    val sn by json.byNullableString
}

class Discord(override val json: JsonObject): JsonModel {
    val private by json.byBool { false }
    val tag by json.byString
}

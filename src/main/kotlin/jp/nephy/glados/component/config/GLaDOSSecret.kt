package jp.nephy.glados.component.config

import com.mongodb.MongoClient
import jp.nephy.penicillin.PenicillinClient

data class GLaDOSSecret(
        val slackIncomingUrl: String,
        val niconicoLoginEmail: String,
        val niconicoLoginPassword: String,
        val soundCloudClientId: String,
        val googleApiKey: String,
        val steamApiKey: String,
        val twiter: PenicillinClient,
        val mongodb: MongoClient
)

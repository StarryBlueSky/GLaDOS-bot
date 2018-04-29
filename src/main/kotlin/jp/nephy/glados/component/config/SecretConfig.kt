package jp.nephy.glados.component.config

import jp.nephy.penicillin.PenicillinClient

data class SecretConfig(
        val slackIncomingUrl: String,
        val niconicoLoginEmail: String,
        val niconicoLoginPassword: String,
        val soundCloudClientId: String,
        val googleApiKey: String,
        val steamApiKey: String,
        val twiter: PenicillinClient
)

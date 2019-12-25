package jp.nephy.glados.clients.loop

import jp.nephy.glados.api.EventModel

interface LoopEventModel: EventModel {
    suspend fun onLoop(event: LoopEvent) {}
}

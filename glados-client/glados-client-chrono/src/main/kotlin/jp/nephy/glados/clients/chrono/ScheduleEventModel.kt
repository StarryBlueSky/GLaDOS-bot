package jp.nephy.glados.clients.chrono

import jp.nephy.glados.api.EventModel

interface ScheduleEventModel: EventModel {
    suspend fun onSchedule(event: ScheduleEvent) {}
}

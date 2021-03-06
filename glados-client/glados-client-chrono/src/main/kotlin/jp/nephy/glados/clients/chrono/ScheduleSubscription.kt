/*
 * The MIT License (MIT)
 *
 *     Copyright (c) 2017-2019 Nephy Project Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package jp.nephy.glados.clients.chrono

import jp.nephy.glados.GLaDOSSubscription
import jp.nephy.glados.api.Plugin
import jp.nephy.glados.api.Priority
import java.util.*
import kotlin.reflect.KFunction

/**
 * ScheduleSubscription.
 */
data class ScheduleSubscription(
    override val plugin: Plugin,
    override val function: KFunction<*>,
    override val annotation: Schedule
): GLaDOSSubscription<Schedule, ScheduleEvent>() {
    override val priority: Priority
        get() = annotation.priority

    /**
     * Scheduled hours.
     */
    val hours: Set<Int> by lazy {
        val hoursADay = 0 until 24
        val h = annotation.hours.toSet() + annotation.multipleHours.flatMap { multipleHour ->
            hoursADay.map {
                it * multipleHour
            }.filter {
                it in hoursADay
            }
        }

        h.ifEmpty { hoursADay }.toSortedSet()
    }

    /**
     * Scheduled minutes.
     */
    val minutes: Set<Int> by lazy {
        val minutesAnHour = 0 until 60
        val m = annotation.minutes.toSet() + annotation.multipleMinutes.flatMap { multipleMinute ->
            minutesAnHour.map {
                it * multipleMinute
            }.filter {
                it in minutesAnHour
            }
        }

        m.ifEmpty { minutesAnHour }.toSortedSet()
    }
}

/**
 * Checks if the subscription matches a calendar.
 */
fun ScheduleSubscription.matches(calendar: Calendar): Boolean {
    return calendar[Calendar.HOUR_OF_DAY] in hours && calendar[Calendar.MINUTE] in minutes
}

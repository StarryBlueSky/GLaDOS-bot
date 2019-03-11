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

import jp.nephy.glados.api.annotations.Priority

/**
 * Indicates that its function should be executed on schedule.
 */
@Target(AnnotationTarget.FUNCTION)
annotation class Schedule(
    /**
     * Hours.
     * e.g. hours = [0, 8, 21, 24] means this subscription will be executed at 0:XX, 8:XX and 21:XX.
     */
    val hours: IntArray = [],

    /**
     * Minutes.
     * e.g. minutes = [0, 30] means this subscription will be executed at X:00 and X:30.
     */
    val minutes: IntArray = [],

    /**
     * Multiple of hours.
     * e.g. multipleHours = [7, 10] means this subscription will be executed at 0:XX, 7:XX, 10:XX, 14:XX, 20:XX and 21:XX.
     */
    val multipleHours: IntArray = [],

    /**
     * Multiple of minutes.
     * e.g. multipleMinutes = [15] means this subscription will be executed at X:00, X:15, X:30 and X:45.
     */
    val multipleMinutes: IntArray = [],

    /**
     * Execution priority.
     */
    val priority: Priority = Priority.Normal
)

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

@file:Suppress("UNUSED")

package jp.nephy.glados.clients

import jp.nephy.glados.api.Event
import jp.nephy.glados.api.Subscription
import jp.nephy.glados.api.SubscriptionClient
import kotlinx.coroutines.launch
import kotlin.reflect.full.isSubclassOf

/**
 * The name of SubscriptionClient.
 */
val SubscriptionClient<*, *, *>.name: String
    get() = this::class.simpleName.orEmpty()

/**
 * The list of Subscriptions.
 */
val <A: Annotation, E: Event, S: Subscription<A, E>> SubscriptionClient<A, E, S>.subscriptions: List<S>
    get() = storage.subscriptions

/**
 * Runs Subscriptions.
 */
inline fun <A: Annotation, E: Event, S: Subscription<A, E>, reified T: E> SubscriptionClient<A, E, S>.runEvent(
    noinline filter: (S) -> Boolean = { true },
    crossinline block: (S) -> T
) {
    subscriptions.asSequence().filter(filter).filter { subscription ->
        subscription.eventClass.isSubclassOf(T::class)
    }.forEach { subscription ->
        launch {
            val event = block(subscription)

            subscription.invoke(event)
        }
    }
}

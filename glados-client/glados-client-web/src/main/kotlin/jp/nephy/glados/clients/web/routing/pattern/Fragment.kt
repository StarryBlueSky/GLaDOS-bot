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

package jp.nephy.glados.clients.web.routing.pattern

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun WebPatternRoutingEvent.booleanFragment(key: String? = null): ReadOnlyProperty<Any?, Boolean?> {
    return object: ReadOnlyProperty<Any?, Boolean?> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): Boolean? {
            return fragments[key ?: property.name]?.toBoolean()
        }
    }
}

fun WebPatternRoutingEvent.booleanFragment(key: String? = null, default: () -> Boolean): ReadOnlyProperty<Any?, Boolean> {
    return object: ReadOnlyProperty<Any?, Boolean> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): Boolean {
            return fragments[key ?: property.name]?.toBoolean() ?: default()
        }
    }
}

fun WebPatternRoutingEvent.intFragment(key: String? = null): ReadOnlyProperty<Any?, Int?> {
    return object: ReadOnlyProperty<Any?, Int?> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): Int? {
            return fragments[key ?: property.name]?.toIntOrNull()
        }
    }
}

fun WebPatternRoutingEvent.intFragment(key: String? = null, default: () -> Int): ReadOnlyProperty<Any?, Int> {
    return object: ReadOnlyProperty<Any?, Int> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): Int {
            return fragments[key ?: property.name]?.toIntOrNull() ?: default()
        }
    }
}

fun WebPatternRoutingEvent.longFragment(key: String? = null): ReadOnlyProperty<Any?, Long?> {
    return object: ReadOnlyProperty<Any?, Long?> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): Long? {
            return fragments[key ?: property.name]?.toLongOrNull()
        }
    }
}

fun WebPatternRoutingEvent.longFragment(key: String? = null, default: () -> Long): ReadOnlyProperty<Any?, Long> {
    return object: ReadOnlyProperty<Any?, Long> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): Long {
            return fragments[key ?: property.name]?.toLongOrNull() ?: default()
        }
    }
}

fun WebPatternRoutingEvent.floatFragment(key: String? = null): ReadOnlyProperty<Any?, Float?> {
    return object: ReadOnlyProperty<Any?, Float?> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): Float? {
            return fragments[key ?: property.name]?.toFloatOrNull()
        }
    }
}

fun WebPatternRoutingEvent.floatFragment(key: String? = null, default: () -> Float): ReadOnlyProperty<Any?, Float> {
    return object: ReadOnlyProperty<Any?, Float> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): Float {
            return fragments[key ?: property.name]?.toFloatOrNull() ?: default()
        }
    }
}

fun WebPatternRoutingEvent.doubleFragment(key: String? = null): ReadOnlyProperty<Any?, Double?> {
    return object: ReadOnlyProperty<Any?, Double?> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): Double? {
            return fragments[key ?: property.name]?.toDoubleOrNull()
        }
    }
}

fun WebPatternRoutingEvent.doubleFragment(key: String? = null, default: () -> Double): ReadOnlyProperty<Any?, Double> {
    return object: ReadOnlyProperty<Any?, Double> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): Double {
            return fragments[key ?: property.name]?.toDoubleOrNull() ?: default()
        }
    }
}

fun WebPatternRoutingEvent.fragment(key: String? = null): ReadOnlyProperty<Any?, String?> {
    return object: ReadOnlyProperty<Any?, String?> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): String? {
            return fragments[key ?: property.name]
        }
    }
}

fun WebPatternRoutingEvent.fragment(key: String? = null, default: () -> String): ReadOnlyProperty<Any?, String> {
    return object: ReadOnlyProperty<Any?, String> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): String {
            return fragments[key ?: property.name] ?: default()
        }
    }
}

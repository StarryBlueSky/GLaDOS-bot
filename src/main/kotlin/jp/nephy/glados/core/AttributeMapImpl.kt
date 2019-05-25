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

package jp.nephy.glados.core

import jp.nephy.glados.api.AttributeMap
import jp.nephy.glados.api.Logger
import jp.nephy.glados.api.of
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap

internal object AttributeMapImpl: AttributeMap {
    private val logger = Logger.of("GLaDOS.AttributeMap")
    private val map = ConcurrentHashMap<String, Any>()
    private val mutex = Mutex()
    
    @Suppress("UNCHECKED_CAST")
    override suspend fun <T: Any> read(key: String): T {
        val attribute = mutex.withLock {
            map[key]
        }

        return if (attribute != null) {
            attribute as? T ?: throw IllegalArgumentException("Attribute[\"$key\"] type is ${attribute::class.qualifiedName}.")
        } else {
            throw UninitializedPropertyAccessException("Attribute[\"$key\"] has not been initialized yet.")
        }
    }

    override suspend fun <T: Any> write(key: String, value: T) {
        if (has(key)) {
            return
        }
        
        mutex.withLock {
            map[key] = value
        }
        logger.debug { "Attribute: \"$key\" が作成されました。" }
    }

    override suspend fun has(key: String): Boolean {
        return mutex.withLock {
            map.containsKey(key)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun <T: Any> remove(key: String): T? {
        return mutex.withLock {
            map.remove(key)
        } as? T
    }
}

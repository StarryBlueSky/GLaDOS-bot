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

package jp.nephy.glados.clients.twitter.config

import jp.nephy.jsonkt.*
import jp.nephy.jsonkt.delegation.*
import jp.nephy.penicillin.endpoints.account
import jp.nephy.penicillin.endpoints.account.verifyCredentials
import jp.nephy.penicillin.extensions.complete
import jp.nephy.penicillin.models.Account

/**
 * TwitterAccount.
 */
data class TwitterAccount(
    /**
     * [JsonObject] for this account.
     */
    override val json: JsonObject,

    /**
     * Json key for this account.
     */
    val key: String
): JsonModel {
    /**
     * Consumer Key.
     */
    val ck: String by string

    /**
     * Consumer Secret.
     */
    val cs: String by string

    /**
     * Access Token.
     */
    val at: String by string

    /**
     * Access Token Secret.
     */
    val ats: String by string

    /**
     * User information.
     */
    val user: Account.VerifyCredentials by lazy {
        client.use {
            it.account.verifyCredentials.complete().result
        }
    }

    override fun hashCode(): Int {
        return key.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return key == (other as? TwitterAccount)?.key
    }
}

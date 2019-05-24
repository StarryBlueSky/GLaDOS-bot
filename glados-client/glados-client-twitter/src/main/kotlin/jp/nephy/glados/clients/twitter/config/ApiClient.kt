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

package jp.nephy.glados.clients.twitter.config

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.apache.Apache
import io.ktor.util.KtorExperimentalAPI
import jp.nephy.glados.api.GLaDOS
import jp.nephy.glados.clients.logger.installHttpClientLogger
import jp.nephy.penicillin.PenicillinClient
import jp.nephy.penicillin.core.emulation.EmulationMode
import jp.nephy.penicillin.core.session.ApiClient
import jp.nephy.penicillin.core.session.SessionBuilder
import jp.nephy.penicillin.core.session.config.*
import jp.nephy.penicillin.endpoints.common.TweetMode

/**
 * Creates default [ApiClient] for this account.
 */
val TwitterAccount.client: ApiClient
    get() = client()

/**
 * Creates default [ApiClient] with [EmulationMode.TwitterForiPhone] for this account.
 */
val TwitterAccount.officialClient: ApiClient
    get() = client(EmulationMode.TwitterForiPhone)

/**
 * Creates [ApiClient] for this account.
 */
@UseExperimental(KtorExperimentalAPI::class)
fun TwitterAccount.client(
    mode: EmulationMode = EmulationMode.None, engine: HttpClientEngineFactory<*> = Apache, block: SessionBuilder.() -> Unit = {}
): ApiClient {
    return PenicillinClient {
        account {
            application(ck, cs)
            token(at, ats)
        }
        dispatcher {
            coroutineContext = GLaDOS.coroutineContext
        }
        httpClient(engine) {
            installHttpClientLogger()
        }
        api {
            emulationMode = mode
            defaultTweetMode = TweetMode.Extended
        }

        block()
    }
}

/*
 * Copyright 2019 Andrius Baruckis www.baruckis.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.baruckis.cryptolive.repository

import android.util.Log
import com.baruckis.cryptolive.api.BitfinexService
import com.baruckis.cryptolive.api.model.Subscribe
import com.baruckis.cryptolive.api.model.Ticker
import com.baruckis.cryptolive.api.model.Unsubscribe
import com.baruckis.cryptolive.data.Summary
import com.baruckis.cryptolive.utils.BITFINEX_WEB_SOCKET_HEARTBEAT
import com.baruckis.cryptolive.utils.LOG_TAG
import com.baruckis.cryptolive.vo.Channel
import com.tinder.scarlet.websocket.WebSocketEvent
import io.reactivex.Flowable
import io.reactivex.processors.BehaviorProcessor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SummaryRepository @Inject constructor(private val bitfinexService: BitfinexService) {

    var summaryProcessor = BehaviorProcessor.create<Summary>()

    var channelId: String = ""

    init {

        bitfinexService.observeWebSocketEvent()
            .filter { it is WebSocketEvent.OnConnectionOpened }
            .subscribe {
                //sendSubscribe()
            }


        bitfinexService.receiveSubscribed()
            .filter { it.channel == Channel.TICKER }
            .subscribe {
                channelId = it.chanId
                //observeTicker(it.chanId)
                Log.d(LOG_TAG, "Subscribed ticker - $it")
            }

        observeTicker()
    }

    fun sendSubscribe() {

        val subscribe = Subscribe(
            channel = Channel.TICKER
        )

        bitfinexService.sendSubscribe(subscribe)
    }

    fun sendUnsubscribe() {

        val unsubscribe = Unsubscribe(
            chanId = channelId
        )

        bitfinexService.sendUnsubscribe(unsubscribe)
    }


    private fun observeTicker(/*channelId: String?*/) {
        bitfinexService.observeTicker()
            .filter { it.size == 11 && it.last() != BITFINEX_WEB_SOCKET_HEARTBEAT }
            .map { response ->
                val ticker = Ticker(
                    channelID = response[0].toInt(),
                    bid = response[1].toDouble(),
                    bid_size = response[2].toDouble(),
                    ask = response[3].toDouble(),
                    ask_size = response[4].toDouble(),
                    daily_change = response[5].toDouble(),
                    daily_change_perc = response[6].toDouble(),
                    last_price = response[7].toDouble(),
                    volume = response[8].toDouble(),
                    high = response[9].toDouble(),
                    low = response[10].toDouble()
                )
                ticker
            }
            .subscribe { ticker: Ticker ->
                Log.d(LOG_TAG, "\uD83D\uDC53 Ticker - $ticker")

                val summary = Summary(
                    price = ticker.last_price.toString(),
                    volume = ticker.volume.toString(),
                    low = ticker.low.toString(),
                    high = ticker.high.toString())

                summaryProcessor.onNext(summary)
            }

    }

    fun observeSummary(): Flowable<Summary> {
        return summaryProcessor
    }

}
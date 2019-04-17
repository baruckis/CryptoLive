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

package com.baruckis.techchallenge.repository

import android.util.Log
import com.baruckis.techchallenge.api.BitfinexService
import com.baruckis.techchallenge.api.model.OrderBook
import com.baruckis.techchallenge.api.model.Subscribe
import com.baruckis.techchallenge.api.model.Unsubscribe
import com.baruckis.techchallenge.utils.BITFINEX_WEB_SOCKET_HEARTBEAT
import com.baruckis.techchallenge.utils.LOG_TAG
import com.baruckis.techchallenge.vo.Channel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderBooksRepository @Inject constructor(private val bitfinexService: BitfinexService) {

    var channelId: String = ""

    init {

        bitfinexService.receiveSubscribed()
            .filter { it.channel == Channel.BOOK }
            .subscribe {
                channelId = it.chanId
                observeOrderBooks(it.chanId)
                Log.d(LOG_TAG, "Subscribed order books - $it")
            }
    }

    fun sendSubscribe() {

        val subscribe = Subscribe(
            channel = Channel.BOOK
        )

        bitfinexService.sendSubscribe(subscribe)
    }

    fun sendUnsubscribe() {

        val unsubscribe = Unsubscribe(
            chanId = channelId
        )

        bitfinexService.sendUnsubscribe(unsubscribe)
    }


    private fun observeOrderBooks(channelId: String) {
        bitfinexService.observeOrderBooks()
            .filter { it.first() == channelId && it.last() != BITFINEX_WEB_SOCKET_HEARTBEAT }
            .map { response ->
                val orderBook = OrderBook(
                    channelID = response[0].toInt(),
                    price = response[1].toFloat(),
                    count = response[2].toFloat(),
                    amount = response[3].toFloat()
                )
                orderBook
            }
            .subscribe { orderBook: OrderBook ->
                Log.d(LOG_TAG, "Order Books - " + orderBook.channelID + " " + orderBook.price)
            }
    }

}
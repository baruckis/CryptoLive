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
import com.baruckis.cryptolive.api.model.OrderBook
import com.baruckis.cryptolive.api.model.Subscribe
import com.baruckis.cryptolive.api.model.Unsubscribe
import com.baruckis.cryptolive.data.Book
import com.baruckis.cryptolive.utils.BITFINEX_WEB_SOCKET_HEARTBEAT
import com.baruckis.cryptolive.utils.LOG_TAG
import com.baruckis.cryptolive.vo.Channel
import io.reactivex.Flowable
import io.reactivex.processors.BehaviorProcessor
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.absoluteValue

@Singleton
class OrderBooksRepository @Inject constructor(private val bitfinexService: BitfinexService) {

    var bookProcessor = BehaviorProcessor.create<Book>()

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
                    price = response[1].toDouble(),
                    count = response[2].toDouble(),
                    amount = response[3].toDouble()
                )
                orderBook
            }
            .subscribe { orderBook: OrderBook ->
                Log.d(LOG_TAG, "\uD83D\uDCDA Order book - $orderBook")

                val book = Book(
                        price = orderBook.price.toString(),
                        amount = orderBook.amount.absoluteValue.toString(),
                        type = if (orderBook.amount > 0) Book.Type.BID else Book.Type.ASK
                )

                bookProcessor.onNext(book)
            }
    }


    fun observeBooks(): Flowable<Book> {
        return bookProcessor
    }

}
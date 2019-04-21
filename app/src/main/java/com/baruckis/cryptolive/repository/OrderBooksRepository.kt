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

import com.baruckis.cryptolive.api.BitfinexService
import com.baruckis.cryptolive.api.model.OrderBook
import com.baruckis.cryptolive.data.Book
import com.baruckis.cryptolive.testing.OpenForTesting
import com.baruckis.cryptolive.utils.BITFINEX_WEB_SOCKET_HEARTBEAT
import com.baruckis.cryptolive.utils.logConsoleVerbose
import com.baruckis.cryptolive.vo.Channel
import io.reactivex.rxkotlin.addTo
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.absoluteValue


@Singleton
@OpenForTesting
class OrderBooksRepository @Inject constructor(private val bitfinexService: BitfinexService) :
    RepoBase<Book>(bitfinexService, Channel.BOOK) {


    override fun onReceiveSubscribed(channelId: String) {
        observeOrderBooks(channelId)
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
                logConsoleVerbose("\uD83D\uDCDA Order book - $orderBook")

                val book = Book(
                    price = orderBook.price.toString(),
                    amount = orderBook.amount.absoluteValue.toString(),
                    type = if (orderBook.amount > 0) Book.Type.BID else Book.Type.ASK
                )

                processor.onNext(book)
            }.addTo(disposables)
    }

}
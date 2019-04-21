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

package com.baruckis.cryptolive.ui.main

import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.ViewModel
import com.baruckis.cryptolive.repository.OrderBooksRepository
import com.baruckis.cryptolive.repository.SummaryRepository
import javax.inject.Inject


class MainViewModel @Inject constructor(
    private val summaryRepository: SummaryRepository,
    private val orderBooksRepository: OrderBooksRepository
) : ViewModel() {

    val bidListData: ArrayList<Array<String>> = ArrayList()
    val askListData: ArrayList<Array<String>> = ArrayList()

    val summary = LiveDataReactiveStreams.fromPublisher(
        summaryRepository.observeData()
    )

    val books = LiveDataReactiveStreams.fromPublisher(
        orderBooksRepository.observeData()/*.throttleLast(UPDATE_INTERVAL_SECONDS, TimeUnit.SECONDS)*/
    )


    fun subscribeTicker() {
        summaryRepository.sendSubscribe()
    }

    fun unsubscribeTicker() {
        summaryRepository.sendUnsubscribe()
    }

    fun subscribeOrderBooks() {
        orderBooksRepository.sendSubscribe()
    }

    fun unsubscribeOrderBooks() {
        orderBooksRepository.sendUnsubscribe()
    }

    private companion object {
        private const val UPDATE_INTERVAL_SECONDS = 1L
    }

}
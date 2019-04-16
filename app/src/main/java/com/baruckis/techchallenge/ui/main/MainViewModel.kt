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

package com.baruckis.techchallenge.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import com.baruckis.techchallenge.BuildConfig
import com.baruckis.techchallenge.api.BitfinexService
import com.baruckis.techchallenge.api.MoshiAdapters
import com.baruckis.techchallenge.api.model.Subscribe
import com.baruckis.techchallenge.api.model.Ticker
import com.baruckis.techchallenge.utils.LOG_TAG
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.messageadapter.moshi.MoshiMessageAdapter
import com.tinder.scarlet.streamadapter.rxjava2.RxJava2StreamAdapterFactory
import com.tinder.scarlet.websocket.ShutdownReason
import com.tinder.scarlet.websocket.WebSocketEvent
import com.tinder.scarlet.websocket.okhttp.OkHttpWebSocket
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor

class MainViewModel : ViewModel() {

    lateinit var bitfinexService: BitfinexService

    init {

        val builder = OkHttpClient.Builder()

        builder.interceptors().add(HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE
        })

        val okHttpClient = builder.build()

        val moshi = Moshi.Builder()
            .add(MoshiAdapters())
            .add(KotlinJsonAdapterFactory())
            .build()

        val protocol = OkHttpWebSocket(
            okHttpClient,
            OkHttpWebSocket.SimpleRequestFactory(
                { Request.Builder().url("wss://api.bitfinex.com/ws/").build() },
                { ShutdownReason.GRACEFUL }
            )
        )
        val configuration = Scarlet.Configuration(
            messageAdapterFactories = listOf(MoshiMessageAdapter.Factory(moshi)),
            streamAdapterFactories = listOf(RxJava2StreamAdapterFactory())
        )
        val scarletInstance = Scarlet(protocol, configuration)
        bitfinexService = scarletInstance.create<BitfinexService>()

        bitfinexService.observeWebSocketEvent()
            .filter { it is WebSocketEvent.OnMessageReceived }
            .subscribe(
            {
                Log.d(LOG_TAG, it.toString())
            }
        )

        bitfinexService.observeTicker()
            .filter{ it.last() != "hb" }
            .map { response ->
                val ticker = Ticker(
                    channelID = response[0].toInt(),
                    bid = response[1].toFloat(),
                    bid_size = response[2].toFloat(),
                    ask = response[3].toFloat(),
                    ask_size = response[4].toFloat(),
                    daily_change = response[5].toFloat(),
                    daily_change_perc = response[6].toFloat(),
                    last_price = response[7].toFloat(),
                    volume = response[8].toFloat(),
                    high = response[9].toFloat(),
                    low = response[10].toFloat()
                )
                ticker
            }
            .subscribe { ticker: Ticker ->
                Log.d(LOG_TAG, ticker.bid.toString())
            }

    }


    fun subscribe() {

        val subscribe = Subscribe(
            event = Subscribe.Type.SUBSCRIBE
        )

        bitfinexService.sendSubscribe(subscribe)

    }

}
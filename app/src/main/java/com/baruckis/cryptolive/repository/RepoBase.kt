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
import com.baruckis.cryptolive.api.model.Subscribe
import com.baruckis.cryptolive.api.model.Unsubscribe
import com.baruckis.cryptolive.utils.logConsoleVerbose
import com.baruckis.cryptolive.vo.Channel
import com.tinder.scarlet.websocket.WebSocketEvent
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.processors.BehaviorProcessor
import io.reactivex.rxkotlin.addTo


abstract class RepoBase<T> constructor(private val bitfinexService: BitfinexService, protected val channel: Channel) {

    protected var processor = BehaviorProcessor.create<T>()
    protected val disposables = CompositeDisposable()

    private var channelId: String = ""


    init {
        bitfinexService.observeWebSocketEvent()
            .filter { it is WebSocketEvent.OnConnectionOpened }
            .subscribe {
                sendSubscribe()
            }.addTo(disposables)

        bitfinexService.receiveSubscribed()
            .filter { it.channel == Channel.BOOK }
            .subscribe {
                channelId = it.chanId
                logConsoleVerbose("Subscribed to ${channel.text} with id $channelId.")
                onReceiveSubscribed(channelId)
            }.addTo(disposables)
    }

    fun sendSubscribe() {
        val subscribe = Subscribe(
            channel = channel
        )
        bitfinexService.sendSubscribe(subscribe)
        logConsoleVerbose("Subscribe to ${channel.text}.")
    }

    fun sendUnsubscribe() {
        val unsubscribe = Unsubscribe(
            chanId = channelId
        )
        bitfinexService.sendUnsubscribe(unsubscribe)
        logConsoleVerbose("Unsubscribe from ${channel.text} with id $channelId.")
    }

    fun observeData(): Flowable<T> {
        return processor
    }

    fun clearDisposables() {
        disposables.clear()
    }

    abstract fun onReceiveSubscribed(channelId: String)

}
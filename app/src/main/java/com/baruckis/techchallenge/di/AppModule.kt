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

package com.baruckis.techchallenge.di

import android.content.Context
import android.util.Log
import com.baruckis.techchallenge.App
import com.baruckis.techchallenge.BuildConfig
import com.baruckis.techchallenge.api.BitfinexService
import com.baruckis.techchallenge.api.MoshiAdapters
import com.baruckis.techchallenge.utils.BITFINEX_WEB_SOCKET_URL
import com.baruckis.techchallenge.utils.LOG_TAG
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.tinder.scarlet.Lifecycle
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.lifecycle.android.AndroidLifecycle
import com.tinder.scarlet.messageadapter.moshi.MoshiMessageAdapter
import com.tinder.scarlet.streamadapter.rxjava2.RxJava2StreamAdapterFactory
import com.tinder.scarlet.websocket.ShutdownReason
import com.tinder.scarlet.websocket.WebSocketEvent
import com.tinder.scarlet.websocket.okhttp.OkHttpWebSocket
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton


@Module(includes = [ViewModelsModule::class])
class AppModule() {

    @Provides
    @Singleton
    fun provideContext(app: App): Context = app

    @Provides
    @Singleton
    fun provideHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()

        builder.interceptors().add(HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE
        })

        return builder.build()
    }

    @Provides
    @Singleton
    fun provideBitfinexService(app: App, okHttpClient: OkHttpClient): BitfinexService {

        val bitfinexService: BitfinexService

        val foreground: Lifecycle = AndroidLifecycle.ofApplicationForeground(app)

        val moshi: Moshi = Moshi.Builder()
            .add(MoshiAdapters())
            .add(KotlinJsonAdapterFactory())
            .build()

        val protocol = OkHttpWebSocket(
            okHttpClient,
            OkHttpWebSocket.SimpleRequestFactory(
                { Request.Builder().url(BITFINEX_WEB_SOCKET_URL).build() },
                { ShutdownReason.GRACEFUL }
            )
        )

        val configuration = Scarlet.Configuration(
            lifecycle = foreground,
            messageAdapterFactories = listOf(MoshiMessageAdapter.Factory(moshi)),
            streamAdapterFactories = listOf(RxJava2StreamAdapterFactory())
        )

        val scarletInstance = Scarlet(protocol, configuration)

        bitfinexService = scarletInstance.create()

        bitfinexService.observeWebSocketEvent()
            .filter { it is WebSocketEvent.OnMessageReceived }
            .subscribe {
                val msg = (it as WebSocketEvent.OnMessageReceived).message
                Log.d(LOG_TAG, "WebSocket message - ${msg.toString()}")
            }

        return bitfinexService
    }

}
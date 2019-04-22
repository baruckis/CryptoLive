# CryptoLive
## Single-screen android app displaying a live BTC/USD order-book.

![cryptolive_screen_recording_resized](https://user-images.githubusercontent.com/2387056/56483577-6e205300-64d3-11e9-8924-b7de59948ec0.gif)

## Description
This app is using the Bitfinex API to display different information on the screen. 
The top part contains a summary of the BTC/USD pair (last price, volume, low, high, change), followed by the live order-book using a WebSocket. 
These informations are available on the Bitfinex documentation at:
1) https://docs.bitfinex.com/v1/reference#ws-public-ticker
2) https://docs.bitfinex.com/v1/reference#ws-public-order-books

## Technology
- Rx/MVVM architecture.
- [Scarlet](https://github.com/Tinder/Scarlet) - a Retrofit inspired WebSocket client for Kotlin, Java, and Android.
  - To do the work libraries [OkHttp](https://square.github.io/okhttp), [rxJava](https://github.com/ReactiveX/RxJava), [rxKotlin](https://github.com/ReactiveX/RxKotlin), [Moshi](https://github.com/square/moshi) and other are used.
  - It provides network change resiliency.
- Android Architecture Components
  - [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) and [LiveData](https://developer.android.com/topic/libraries/architecture/livedata).
  - [ReactiveStreams support for LiveData](https://developer.android.com/reference/android/arch/lifecycle/LiveDataReactiveStreams).
- Dependency injection provided by [Dagger](https://github.com/google/dagger).
- [Mockito-Kotlin](https://github.com/nhaarman/mockito-kotlin) for testing.

## License

    Copyright 2019 Andrius Baruckis www.baruckis.com

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

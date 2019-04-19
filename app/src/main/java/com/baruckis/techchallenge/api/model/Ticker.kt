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

package com.baruckis.techchallenge.api.model

data class Ticker(
    val channelID: Int,
    val bid: Double,
    val bid_size: Double,
    val ask: Double,
    val ask_size: Double,
    val daily_change: Double,
    val daily_change_perc: Double,
    val last_price: Double,
    val volume: Double,
    val high: Double,
    val low: Double
)
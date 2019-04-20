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

package com.baruckis.cryptolive.api

import com.baruckis.cryptolive.vo.Channel
import com.baruckis.cryptolive.vo.Event
import com.baruckis.cryptolive.vo.Pair
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson


class MoshiAdapters {

    @FromJson
    fun eventFromJson(string: String): Event {
        return Event.values().find { it.text == string }!!
    }

    @ToJson
    fun eventToJson(data: Event): String {
        return data.text
    }


    @FromJson
    fun channelFromJson(string: String): Channel {
        return Channel.values().find { it.text == string }!!
    }

    @ToJson
    fun channelToJson(data: Channel): String {
        return data.text
    }


    @FromJson
    fun pairFromJson(string: String): Pair {
        return Pair.values().find { it.text == string }!!
    }

    @ToJson
    fun pairToJson(data: Pair): String {
        return data.text
    }

}
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

package com.baruckis.techchallenge.api

import com.baruckis.techchallenge.api.model.Subscribe
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

class MoshiAdapters {

    @FromJson
    fun subscribeTypeFromJson(string: String): Subscribe.Type {
        return Subscribe.Type.values().find { it.text == string }!!
    }

    @ToJson
    fun subscribeTypeToJson(data: Subscribe.Type): String {
        return data.text
    }

}
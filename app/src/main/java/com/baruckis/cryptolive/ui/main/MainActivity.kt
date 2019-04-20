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

import android.os.Bundle
import android.text.SpannableStringBuilder
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.bold
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.baruckis.cryptolive.R
import com.baruckis.cryptolive.data.Summary
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        supportActionBar?.subtitle = getString(R.string.app_subtitle)


        val viewModel =
                ViewModelProviders.of(this, viewModelFactory).get(MainViewModel::class.java)

        viewModel.summary.observe(this, Observer<Summary> { data ->
            data?.let {

                price.text = SpannableStringBuilder()
                        .append(getString(R.string.ticker_price)).bold { append(it.price) }

                volume.text = SpannableStringBuilder()
                        .append(getString(R.string.ticker_volume)).bold { append(it.volume) }

                low.text = SpannableStringBuilder()
                        .append(getString(R.string.ticker_low)).bold { append(it.low) }

                high.text = SpannableStringBuilder()
                        .append(getString(R.string.ticker_high)).bold { append(it.high) }
            }
        })

    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = dispatchingAndroidInjector

}

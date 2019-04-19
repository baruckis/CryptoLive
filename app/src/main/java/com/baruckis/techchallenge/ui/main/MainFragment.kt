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

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.baruckis.techchallenge.R
import com.baruckis.techchallenge.data.Book
import com.baruckis.techchallenge.databinding.FragmentMainBinding
import com.baruckis.techchallenge.di.Injectable
import com.baruckis.techchallenge.utils.BOOK_ROWS
import com.baruckis.techchallenge.utils.LOG_TAG
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*
import javax.inject.Inject


class MainFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var binding: FragmentMainBinding


    private lateinit var bidListTextViews: List<Array<TextView>>
    private lateinit var askListTextViews: List<Array<TextView>>

    private val bidListData: ArrayList<Array<String>> = ArrayList()
    private val askListData: ArrayList<Array<String>> = ArrayList()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        val view: View = binding.root

        bidListTextViews = createTable(view.bid_table, BOOK_ROWS)
        askListTextViews = createTable(view.ask_table, BOOK_ROWS)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.let { it ->

            val viewModel = ViewModelProviders.of(it, viewModelFactory).get(MainViewModel::class.java)

            subscribe_ticker.setOnClickListener {

                viewModel.subscribeTicker()
            }

            unsubscribe_ticker.setOnClickListener {

                viewModel.unsubscribeTicker()
            }

            subscribe_order_books.setOnClickListener {

                viewModel.subscribeOrderBooks()
            }

            unsubscribe_order_books.setOnClickListener {

                viewModel.unsubscribeOrderBooks()
            }


            viewModel.books.observe(this, Observer<Book> { data ->
                data?.let {

                    Log.d(LOG_TAG, "\uD83D\uDCD7 Book - " + it.type + " " + it.price + " " + it.amount)

                    onBookObserve(it)
                }
            })

        }

    }


    private fun createTable(tableLayout: TableLayout, rows: Int): List<Array<TextView>> {

        val list: ArrayList<Array<TextView>> = ArrayList()

        for (i in 0 until rows) {

            val row = TableRow(tableLayout.context)
            row.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            )

            val firstTextView: TextView = createTableTextView(tableLayout.context, Gravity.START, "")
            val secondTextView: TextView = createTableTextView(tableLayout.context, Gravity.END, "")

            list.add(arrayOf(firstTextView, secondTextView))

            row.addView(firstTextView)
            row.addView(secondTextView)

            tableLayout.addView(row)
        }

        return list
    }

    private fun createTableTextView(context: Context, gravity: Int, text: String): TextView {

        val lp: TableRow.LayoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT, 1.0f
        )

        val textView = TextView(context)
        textView.apply {
            layoutParams = lp
            this.gravity = gravity
            this.text = text
        }

        return textView
    }


    private fun onBookObserve(book: Book) {

        fun fillTextViewsWithData(texts: Array<String>,
                     textViewsList: List<Array<TextView>>,
                     dataList: ArrayList<Array<String>>,
                     dataListSizeLimit: Int = BOOK_ROWS ) {

            dataList.add(texts)
            if (dataList.size > dataListSizeLimit) dataList.removeAt(0)
            val reversedDataList = dataList.reversed()

            for (i in 0 until reversedDataList.size) {
                for (j in 0 until textViewsList[i].size) {
                    textViewsList[i][j].text = reversedDataList[i][j]
                }
            }
        }

        when (book.type) {

            Book.Type.BID -> {
                fillTextViewsWithData(arrayOf(book.amount, book.price), bidListTextViews, bidListData)
            }

            Book.Type.ASK -> {
                fillTextViewsWithData(arrayOf(book.price, book.amount), askListTextViews, askListData)
            }

        }

    }

}
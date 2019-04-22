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

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.baruckis.cryptolive.R
import com.baruckis.cryptolive.data.Book
import com.baruckis.cryptolive.databinding.FragmentMainBinding
import com.baruckis.cryptolive.di.Injectable
import com.baruckis.cryptolive.utils.BOOK_ROWS
import kotlinx.android.synthetic.main.fragment_main.view.*
import javax.inject.Inject


class MainFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var viewModel: MainViewModel

    lateinit var binding: FragmentMainBinding

    private lateinit var bidListTextViews: List<Array<TextView>>
    private lateinit var askListTextViews: List<Array<TextView>>


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        val view: View = binding.root

        bidListTextViews =
            createTable(
                view.bid_table, BOOK_ROWS, 0.6f, 0.4f,
                secondTextColorResourceId = R.color.colorBid
            )

        askListTextViews =
            createTable(
                view.ask_table, BOOK_ROWS, 0.4f, 0.6f,
                firstTextColorResourceId = R.color.colorAsk
            )

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.let { it ->

            viewModel = ViewModelProviders.of(it, viewModelFactory).get(MainViewModel::class.java)

            fillTextViewsWithData(bidListTextViews, viewModel.bidListData)
            fillTextViewsWithData(askListTextViews, viewModel.askListData)


            viewModel.books.observe(this, Observer<Book> { data ->
                data?.let {
                    onBookObserve(it)
                }
            })

        }

    }

    private fun createTable(
        tableLayout: TableLayout,
        rows: Int,
        firstTextWeight: Float,
        secondTextWeight: Float,
        @ColorRes firstTextColorResourceId: Int? = null,
        @ColorRes secondTextColorResourceId: Int? = null
    ): List<Array<TextView>> {

        val list: ArrayList<Array<TextView>> = ArrayList()

        for (i in 0 until rows) {

            val row = TableRow(tableLayout.context)
            row.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            val firstTextView: TextView =
                createTableTextView(
                    tableLayout.context,
                    Gravity.START,
                    firstTextWeight,
                    firstTextColorResourceId
                )

            val secondTextView: TextView =
                createTableTextView(
                    tableLayout.context,
                    Gravity.END,
                    secondTextWeight,
                    secondTextColorResourceId
                )

            list.add(arrayOf(firstTextView, secondTextView))

            row.addView(firstTextView)
            row.addView(secondTextView)

            tableLayout.addView(row)
        }

        return list
    }

    private fun createTableTextView(
        context: Context,
        gravity: Int,
        weight: Float,
        @ColorRes colorResourceId: Int? = null,
        text: String = ""
    ): TextView {

        val lp: TableRow.LayoutParams = TableRow.LayoutParams(
            TableRow.LayoutParams.WRAP_CONTENT,
            TableRow.LayoutParams.WRAP_CONTENT, weight
        )

        val textView = TextView(context)
        textView.apply {
            layoutParams = lp
            this.gravity = gravity
            this.text = text
        }

        val padding = resources.getDimensionPixelOffset(R.dimen.padding_default)
        textView.setPadding(padding, 0, padding, 0)

        val textSize = resources.getDimension(R.dimen.text_book)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)

        if (colorResourceId != null)
            textView.setTextColor(ContextCompat.getColor(context, colorResourceId))

        return textView
    }


    private fun onBookObserve(book: Book) {

        when (book.type) {

            Book.Type.BID -> {
                fillTextViewsWithData(bidListTextViews, viewModel.bidListData, arrayOf(book.amount, book.price))
            }

            Book.Type.ASK -> {
                fillTextViewsWithData(askListTextViews, viewModel.askListData, arrayOf(book.price, book.amount))
            }
        }
    }

    private fun fillTextViewsWithData(
        textViewsList: List<Array<TextView>>,
        dataList: ArrayList<Array<String>>,
        newTexts: Array<String>? = null,
        dataListSizeLimit: Int = BOOK_ROWS
    ) {
        if (newTexts != null) {
            dataList.add(newTexts)
            if (dataList.size > dataListSizeLimit) dataList.removeAt(0)
        }
        if (dataList.isEmpty()) return
        val reversedDataList = dataList.reversed()

        for (i in 0 until reversedDataList.size) {
            for (j in 0 until textViewsList[i].size) {
                textViewsList[i][j].text = reversedDataList[i][j]
            }
        }
    }

}
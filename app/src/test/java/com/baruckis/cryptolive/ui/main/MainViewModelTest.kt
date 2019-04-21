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

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.*
import com.baruckis.cryptolive.data.Book
import com.baruckis.cryptolive.data.Summary
import com.baruckis.cryptolive.repository.OrderBooksRepository
import com.baruckis.cryptolive.repository.SummaryRepository
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Flowable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.JUnit4


@RunWith(JUnit4::class)
class MainViewModelTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()


    companion object {

        @BeforeClass
        @JvmStatic
        fun initialize() {

            // Override AndroidSchedulers.mainThread() which doesn't work in unit tests.
            RxAndroidPlugins.setInitMainThreadSchedulerHandler {
                Schedulers.trampoline()
            }
        }
    }


    private val lifecycleOwner: LifecycleOwner = mock()
    private val lifecycleRegistry = LifecycleRegistry(lifecycleOwner).also {
        whenever(lifecycleOwner.lifecycle).thenReturn(it)
    }

    private val summaryRepository: SummaryRepository = mock()
    private val orderBooksRepository: OrderBooksRepository = mock()

    private lateinit var mainViewModel: MainViewModel

    private val summaryObserver: Observer<Summary?> = mock()
    private val booksObserver: Observer<Book?> = mock()

    private val testSampleSummary: Summary =
        Summary("5125.13726932", "7704.0071242300000006", "5049.30627064", "5163.0")
    private val testSampleBook: Book = Book("5126.6", "0.2", Book.Type.ASK)


    @Before
    fun setUp() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        whenever(summaryRepository.observeData()).thenReturn(Flowable.just(testSampleSummary))
        whenever(orderBooksRepository.observeData()).thenReturn(Flowable.just(testSampleBook))

        mainViewModel = MainViewModel(summaryRepository, orderBooksRepository)

        mainViewModel.summary.observeForever(summaryObserver)
        mainViewModel.books.observeForever(booksObserver)
    }

    @After
    fun tearDown() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
    }

    @Test
    fun getSummary() {
        with(mainViewModel) {
            summary
                .toFlowable(lifecycleOwner)
                .test()
                .assertValue(testSampleSummary)
        }
    }

    @Test
    fun getBooks() {
        with(mainViewModel) {
            books
                .toFlowable(lifecycleOwner)
                .test()
                .assertValue(testSampleBook)
        }
    }


    private fun <T> LiveData<T>.toFlowable(lifecycleOwner: LifecycleOwner): Flowable<T> =
        Flowable.fromPublisher(LiveDataReactiveStreams.toPublisher(lifecycleOwner, this))

}
/*
 * MIT License
 *
 * Copyright (c) 2023.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.*

class TimeLineDataKtTest {

    private lateinit var dataList: List<Pojo>

    @BeforeEach
    fun setUp() {
        dataList = listOf(
            Pojo(date = LocalDateTime.of(2021, 8, 1, 1, 1), value = "1"),
            Pojo(date = LocalDateTime.of(2021, 8, 1, 1, 1), value = "2"),
            Pojo(date = LocalDateTime.of(2021, 8, 1, 1, 1), value = "3"),
            Pojo(date = LocalDateTime.of(2021, 8, 1, 1, 1), value = "3"),
        )
    }

    @AfterEach
    fun tearDown() {
        dataList = emptyList()
    }

    @Test
    fun sampleTest() {

        val actual = TimeLineData.Builder<Pojo>()
            .dataList(dataList)
            .k {
                dateStr(
                    LocalDateTime.ofInstant(it.date.toInstant(ZoneOffset.of("+8")), ZoneId.systemDefault()),
                    DateType.MONTH
                )
            }
            .v { BigDecimal.valueOf(it.value.toDouble()) }
            .start(LocalDateTime.of(2020, 8, 1, 1, 1))
            .end(LocalDateTime.of(2021, 8, 1, 1, 1))
            .build()
            .data

        val expect = "{2020-08=0, 2020-09=0, 2020-10=0, 2020-11=0, 2020-12=0, 2021-01=0, 2021-02=0, 2021-03=0, 2021-04=0, 2021-05=0, 2021-06=0, 2021-07=0, 2021-08=9.0}"

        assert(Objects.equals(expect, actual.toString()))
    }
}
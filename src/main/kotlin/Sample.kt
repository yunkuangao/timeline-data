import java.math.BigDecimal
import java.time.LocalDateTime

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

/**
 * pojo of sample
 *
 * @author cloud yun@yunkuangao.com
 */
data class Pojo(
    var date: LocalDateTime,
    var value: String,
)

/**
 * sample of timeLineData build
 *
 * @author cloud yun@yunkuangao.com
 */
fun sample() {
    val dataList: List<Pojo> = listOf(
        Pojo(LocalDateTime.now(), "1"),
        Pojo(LocalDateTime.now().minusSeconds(3600 * 24 * 1), "2"),
        Pojo(LocalDateTime.now().minusSeconds(3600 * 24 * 1), "3"),
        Pojo(LocalDateTime.now().minusSeconds(3600 * 24 * 2), "3")
    )

    val result: Map<String, BigDecimal> = TimeLineData.Builder<Pojo>()
        .dataList(dataList)
        .k { dateStr(it.date, DateType.MONTH) }
        .v { BigDecimal.valueOf(it.value.toDouble()) }
        .start(LocalDateTime.of(2020, 8, 1, 1, 1))
        .end(LocalDateTime.of(2021, 8, 1, 1, 1))
        .build()
        .data
}
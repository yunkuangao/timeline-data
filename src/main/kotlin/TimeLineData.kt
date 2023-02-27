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

import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.function.Function
import java.util.function.UnaryOperator
import java.util.stream.Collectors
import java.util.stream.Stream

/**
 *
 * build time-line data
 *
 * @sample sample
 *
 * @author yunkuangao
 */
open class TimeLineData<V> private constructor() {
    private var dataList: List<V> = listOf()
    private var perFilter: Function<V, Boolean> = Function { true }
    private var postFilter: Function<Map<String, BigDecimal>, Boolean> = Function { true }
    private var k: Function<V, String> = Function { "" }
    private var v: Function<V, BigDecimal> = Function { BigDecimal.ZERO }
    private var dateType: DateType = DateType.YEAR
    private var start: LocalDateTime = LocalDateTime.now()
    private var end: LocalDateTime = LocalDateTime.now()
    private var init: BigDecimal = BigDecimal.ZERO

    protected fun setDataList(dataList: List<V>) {
        this.dataList = dataList
    }

    protected fun setPerFilter(perFilter: Function<V, Boolean>) {
        this.perFilter = perFilter
    }

    protected fun setPostFilter(postFilter: Function<Map<String, BigDecimal>, Boolean>) {
        this.postFilter = postFilter
    }

    protected fun setK(k: Function<V, String>) {
        this.k = k
    }

    protected fun setV(v: Function<V, BigDecimal>) {
        this.v = v
    }

    protected fun setDateType(dateType: DateType) {
        this.dateType = dateType
    }

    protected fun setStart(start: LocalDateTime) {
        this.start = start
    }

    protected fun setEnd(end: LocalDateTime) {
        this.end = end
    }

    protected fun setInit(init: BigDecimal) {
        this.init = init
    }

    /**
     * @return get timeline data
     */
    val data: Map<String, BigDecimal>
        get() {
            val temp: MutableMap<String, BigDecimal> = getTimeLineTemplate(start, end, dateType, init).toMutableMap()
            temp.putAll(dataList.filter { perFilter.apply(it) }.map { mapOf(Pair(k.apply(it), v.apply(it))) }.filter { postFilter.apply(it) }
                .reduce { older, newer -> older.add(newer) })
            return temp.toSortedMap()
        }

    /**
     * map extend function
     */
    private fun Map<String, BigDecimal>.add(other: Map<String, BigDecimal>): Map<String, BigDecimal> {
        val temp = this.toMutableMap()
        other.map { (k, v) -> temp.merge(k, v) { older: BigDecimal, newer: BigDecimal -> older.add(newer) } }
        return temp

    }

    /**
     * dataLineBuilder
     *
     * @author cloud yun@yunkuangao.com
     */
    internal class Builder<V> {
        private var dataList: List<V> = listOf()
        private var perFilter = Function { _: V -> true }
        private var postFilter = Function { _: Map<String, BigDecimal> -> true }
        private var k: Function<V, String> = Function { "" }
        private var v: Function<V, BigDecimal> = Function { BigDecimal.ZERO }
        private var dateType = DateType.MONTH
        private var start = LocalDateTime.now().minusYears(1)
        private var end = LocalDateTime.now()
        private var init = BigDecimal.ZERO

        fun perFilter(perFilter: Function<V, Boolean>): Builder<V> {
            this.perFilter = perFilter
            return this
        }

        fun postFilter(postFilter: Function<Map<String, BigDecimal>, Boolean>): Builder<V> {
            this.postFilter = postFilter
            return this
        }

        fun dataList(dataList: List<V>): Builder<V> {
            this.dataList = dataList
            return this
        }

        fun k(k: Function<V, String>): Builder<V> {
            this.k = k
            return this
        }

        fun v(v: Function<V, BigDecimal>): Builder<V> {
            this.v = v
            return this
        }

        fun dateType(dateType: DateType): Builder<V> {
            this.dateType = dateType
            return this
        }

        fun start(start: LocalDateTime): Builder<V> {
            this.start = start
            return this
        }

        fun end(end: LocalDateTime): Builder<V> {
            this.end = end
            return this
        }

        fun init(init: BigDecimal): Builder<V> {
            this.init = init
            return this
        }

        /**
         * @return return a TimeLineData object
         */
        fun build(): TimeLineData<V> {
            return object : TimeLineData<V>() {
                init {
                    setDataList(dataList)
                    setK(k)
                    setV(v)
                    setPerFilter(perFilter)
                    setPostFilter(postFilter)
                    setDateType(dateType)
                    setStart(start)
                    setEnd(end)
                    setInit(init)
                }
            }
        }
    }
}

/**
 * time range
 */
enum class DateType {
    YEAR, MONTH, DAY, HOUR;
}

/**
 * @return get string by dateType
 *
 * @author cloud yun@yunkuangao.com
 *
 * @param date LocalDateTime
 * @param dateType DateType
 */
fun dateStr(date: LocalDateTime, dateType: DateType): String {
    val dtf: DateTimeFormatter = when (dateType) {
        DateType.MONTH -> DateTimeFormatter.ofPattern("yyyy-MM")
        DateType.YEAR -> DateTimeFormatter.ofPattern("yyyy")
        DateType.DAY -> DateTimeFormatter.ofPattern("yyyy-MM-dd")
        DateType.HOUR -> DateTimeFormatter.ofPattern("yyyy-MM-dd HH")
    }
    return dtf.format(date)
}

/**
 * @return return a template for timeline data
 *
 * @author cloud yun@yunkuangao.com
 *
 * @param start start time
 * @param end end time
 * @param dateType range statistics, default: DateType.YEAR
 * @param init initialization value, default: BigDecimal.ZERO
 */
fun getTimeLineTemplate(
    start: LocalDateTime,
    end: LocalDateTime,
    dateType: DateType = DateType.YEAR,
    init: BigDecimal = BigDecimal.ZERO,
): Map<String, BigDecimal> {
    val adder: UnaryOperator<LocalDateTime>
    val maxSize: Long
    val key = Function { it: LocalDateTime -> dateStr(it, dateType) }
    when (dateType) {
        DateType.DAY -> {
            adder = UnaryOperator { it.plusDays(1) }
            maxSize = ChronoUnit.DAYS.between(start, end) + 1
        }

        DateType.HOUR -> {
            adder = UnaryOperator { it.plusHours(1) }
            maxSize = ChronoUnit.HOURS.between(start, end) + 1
        }

        DateType.YEAR -> {
            adder = UnaryOperator { it.plusYears(1) }
            maxSize = ChronoUnit.YEARS.between(start, end) + 1
        }

        DateType.MONTH -> {
            adder = UnaryOperator { it.plusMonths(1) }
            maxSize = ChronoUnit.MONTHS.between(start, end) + 1
        }
    }
    return Stream.iterate(start, adder).limit(maxSize).map(key).collect(Collectors.toMap({ obj: String -> obj }, { init }))
}

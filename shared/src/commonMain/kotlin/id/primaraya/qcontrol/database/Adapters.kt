package id.primaraya.qcontrol.database

import app.cash.sqldelight.ColumnAdapter

val IntAdapter = object : ColumnAdapter<Int, Long> {
    override fun decode(databaseValue: Long): Int = databaseValue.toInt()
    override fun encode(value: Int): Long = value.toLong()
}

val BooleanAdapter = object : ColumnAdapter<Boolean, Long> {
    override fun decode(databaseValue: Long): Boolean = databaseValue == 1L
    override fun encode(value: Boolean): Long = if (value) 1L else 0L
}

val DoubleAdapter = object : ColumnAdapter<Double, Double> {
    override fun decode(databaseValue: Double): Double = databaseValue
    override fun encode(value: Double): Double = value
}

val LongAdapter = object : ColumnAdapter<Long, Long> {
    override fun decode(databaseValue: Long): Long = databaseValue
    override fun encode(value: Long): Long = value
}

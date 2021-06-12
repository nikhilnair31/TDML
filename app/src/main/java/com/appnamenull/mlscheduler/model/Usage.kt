package com.appnamenull.mlscheduler.model

import java.io.Serializable

class Usage : Serializable {

    val TABLE_NAME = "usage"
    val COLUMN_ID = "id"
    val COLUMN_HOUR= "hour"
    val COLUMN_FGTIME = "fgtime"
    val COLUMN_TXBYTES= "txbytes"

    private var id = 0
    private var hour: Int? = null
    private var fgtime: Double? = null
    private var txbytes: Int? = null

    val CREATE_TABLE = ("CREATE TABLE " + TABLE_NAME + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_HOUR + " TEXT,"
            + COLUMN_FGTIME + " DOUBLE," + COLUMN_TXBYTES + " TEXT" + ")")

    fun Usage(id: Int, hour: Int?, fgtime: Double?, txbytes: Int?) {
        this.id = id
        this.hour = hour
        this.fgtime = fgtime
        this.txbytes = txbytes
    }
}
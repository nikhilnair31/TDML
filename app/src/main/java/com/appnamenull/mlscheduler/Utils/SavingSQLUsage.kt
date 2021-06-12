package com.appnamenull.mlscheduler.Utils

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.appnamenull.mlscheduler.model.Usage

class SavingSQLUsage(context: Context) : SQLiteOpenHelper( context, DATABASE_NAME, null, DATABASE_VERSION ) {

    val TABLE_NAME = "usage"
    val COLUMN_ID = "id"
    val COLUMN_HOUR= "hour"
    val COLUMN_FGTIME = "fgtime"
    val COLUMN_TXBYTES= "txbytes"
    val CREATE_TABLE = ("CREATE TABLE " + TABLE_NAME + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_HOUR + " TEXT,"
            + COLUMN_FGTIME + " DOUBLE," + COLUMN_TXBYTES + " TEXT" + ")")
    private val mPreferences by lazy { context.getSharedPreferences("firstTime", Context.MODE_PRIVATE) }
    private val usage = Usage()

    override fun onCreate(db: SQLiteDatabase) {
        println("SAVINGSQLUSAGE onCreate")
        db.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade( db: SQLiteDatabase, oldVersion: Int, newVersion: Int ) {
        println("SAVINGSQLUSAGE onUpgrade")
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
    }

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "usage_db"
    }

    fun getLastId(): Int {
        println("SAVINGSQLUSAGE getLastId")
        val db = this.writableDatabase
        val cursor = db.query(TABLE_NAME, arrayOf(COLUMN_ID), null, null, null, null, null)
        cursor.moveToLast()
        return cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
    }

    fun insertAll(hour: Int?, fgtime: Double?, txbytes: Int?): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_HOUR, hour)
        if (fgtime != null) {
            values.put(COLUMN_FGTIME, (Math.round(fgtime * 100.0)/100.0) )
        }
        values.put(COLUMN_TXBYTES, txbytes)
        val id = db.insert(TABLE_NAME, null, values)
        println("SAVINGSQLUSAGE insertAll values : $values")
        db.close()
        return id
    }

    fun getUsageTillNow(): DoubleArray {
        val fgtime = mutableListOf<Double>()
        val selectQuery =  "SELECT  * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_ID + " ASC"
        val db = this.writableDatabase
        val cursor: Cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            do {
                fgtime.add(cursor.getDouble(cursor.getColumnIndex(COLUMN_FGTIME)))
                //println("SAVINGSQLUSAGE getCurrentUsage fgtime : ${cursor.getColumnIndex(usage.COLUMN_FGTIME)}")
            } while (cursor.moveToNext())
            println("SAVINGSQLUSAGE getUsageTillNow fg : $fgtime")
        }
        mPreferences!!.edit().putBoolean("firstTime", false).apply()
        cursor.close()
        db.close()
        return fgtime.toDoubleArray()
    }

    fun getAllUsages(): ArrayList<String>? {
        var fg = ""
        val fgtime = mutableListOf<Double>()
        val totalusage: ArrayList<String> = ArrayList()
        val selectQuery =  "SELECT  * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_ID + " ASC"
        val db = this.writableDatabase
        val cursor: Cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            do {
                println("SAVINGSQLUSAGE getAllUsages ID : ${cursor.getColumnIndex(COLUMN_ID)}\tHOUR : ${cursor.getColumnIndex(COLUMN_HOUR)}\t" +
                        "FGTIME : ${cursor.getColumnIndex(COLUMN_FGTIME)}\tTXBYTES : ${cursor.getColumnIndex(COLUMN_TXBYTES)}")
                fg += cursor.getInt(cursor.getColumnIndex(COLUMN_FGTIME)).toString()+","
                val hourlyusage = cursor.getInt(cursor.getColumnIndex(COLUMN_HOUR)).toString() + " :\t" +
                                    cursor.getDouble(cursor.getColumnIndex(COLUMN_FGTIME)).toString() + " Minutes\t" +
                                        cursor.getInt(cursor.getColumnIndex(COLUMN_TXBYTES)).toString() + " Bytes\t"
                fgtime.add(cursor.getDouble(cursor.getColumnIndex(COLUMN_FGTIME)))
                totalusage.add(hourlyusage)
            } while (cursor.moveToNext())
            println("SAVINGSQLUSAGE getAllUsages fg : $fgtime")
        }
        cursor.close()
        db.close()
        return totalusage
    }

    fun deleteAllUsage() {
        val db = this.writableDatabase
        println("SAVINGSQLUSAGE deleteAllUsage")
        db.delete(TABLE_NAME, null, null);
        db.close()
    }

}
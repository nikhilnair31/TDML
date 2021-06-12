package com.appnamenull.mlscheduler.Utils

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.appnamenull.mlscheduler.model.Task

class SavingSQLTasks(context: Context) : SQLiteOpenHelper( context, DATABASE_NAME, null, DATABASE_VERSION ) {

    val TABLE_NAME = "tasks"
    val COLUMN_ID = "id"
    val COLUMN_TASK= "task"
    val COLUMN_DETAILS = "details"
    val COLUMN_DATETIME = "datetime"
    val COLUMN_COMPSTAT = "compstat"
    val COLUMN_DELSTAT= "delstat"
    val CREATE_TABLE = ("CREATE TABLE $TABLE_NAME ( $COLUMN_ID  INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_TASK TEXT, $COLUMN_DETAILS TEXT, $COLUMN_DATETIME DATETIME DEFAULT CURRENT_TIMESTAMP ) ")
    private val task = Task()

    override fun onCreate(db: SQLiteDatabase) {
        println("SAVINGSQLTASKS onCreate")
        db.execSQL(CREATE_TABLE)
        db.execSQL(" ALTER TABLE $TABLE_NAME ADD COLUMN $COLUMN_COMPSTAT TEXT")
        db.execSQL(" ALTER TABLE $TABLE_NAME ADD COLUMN $COLUMN_DELSTAT TEXT")
    }

    // Upgrading database
    override fun onUpgrade( db: SQLiteDatabase, oldVersion: Int, newVersion: Int ) {
        println("SAVINGSQLTASKS onUpgrade oldVersion:$oldVersion newVersion-$newVersion")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        when (newVersion) {
            1 -> {
                db.execSQL("ALTER TABLE myTable ADD COLUMN myNewColumn TEXT")
            }
            2 -> {
                db.execSQL(CREATE_TABLE)
                db.execSQL(" ALTER TABLE $TABLE_NAME ADD COLUMN $COLUMN_COMPSTAT TEXT")
                db.execSQL(" ALTER TABLE $TABLE_NAME ADD COLUMN $COLUMN_DELSTAT TEXT")
            }
        }
        onCreate(db)
    }

    companion object {
        private const val DATABASE_VERSION = 2
        private const val DATABASE_NAME = "tasks_db"
    }

    fun insertAll(taskname: String?, details: String?, datetime: String?, compstat: String?, delstat: String?): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_TASK, taskname)
        values.put(COLUMN_DETAILS, details)
        values.put(COLUMN_DATETIME, datetime)
        values.put(COLUMN_COMPSTAT, compstat)
        values.put(COLUMN_DELSTAT, delstat)
        val id = db.insert(TABLE_NAME, null, values).toInt()
        println("SAVINGSQLTASKS insertAll id : $id\tvalues : $values")
        db.close()
        return id
    }

    fun updateAll(task: Task, taskname: String?, details: String?, datetime: String?): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_TASK, taskname)
        values.put(COLUMN_DETAILS, details)
        values.put(COLUMN_DATETIME, datetime)
        val id =  db.update( TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(java.lang.String.valueOf(task.getId())) )
        println("SAVINGSQLTASKS updateAll id : $id\tvalues : $values")
        db.close()
        return id
    }

    fun updateTask(task: Task, compstat: String?, delstat: String?): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_COMPSTAT, compstat)
        values.put(COLUMN_DELSTAT, delstat)
        val id =  db.update( TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(java.lang.String.valueOf(task.getId())) )
        println("SAVINGSQLTASKS updateTask id : $id\tvalues : $values\ttask.getId() : ${task.getId()}\ttask.getTask() : ${task.getTask()}")
        db.close()
        return id
    }

    fun getAllIncompTask(): ArrayList<Task>? {
        val tasks: ArrayList<Task> = ArrayList()
        //val selectQuery = "SELECT  * FROM $TABLE_NAME ORDER BY $COLUMN_ID DESC"
        val selectQuery = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_COMPSTAT = 'NOT COMP' ORDER BY $COLUMN_ID DESC"
        val db = this.writableDatabase
        val cursor: Cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            do {
                val task = Task()
                task.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)))
                task.setTask(cursor.getString(cursor.getColumnIndex(COLUMN_TASK)))
                task.setDetails(cursor.getString(cursor.getColumnIndex(COLUMN_DETAILS)))
                task.setDateTime(cursor.getString(cursor.getColumnIndex(COLUMN_DATETIME)))
                task.setCompStat(cursor.getString(cursor.getColumnIndex(COLUMN_COMPSTAT)))
                task.setDelStat(cursor.getString(cursor.getColumnIndex(COLUMN_DELSTAT)))
                println("SAVINGSQLTASKS getAllIncompTask ID : ${task.getId()}\tCOLUMN_TASK : ${task.getTask()}\t" +
                        "COLUMN_DETAILS : ${task.getDetails()}\tCOLUMN_DATETIME : ${task.getDateTime()}\t"+
                        "COLUMN_COMPSTAT : ${task.getCompStat()}\tCOLUMN_DELSTAT : ${task.getDelStat()}")
                tasks.add(task)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return tasks
    }

    fun getAllCompTask(): ArrayList<Task>? {
        val tasks: ArrayList<Task> = ArrayList()
        val selectQuery = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_COMPSTAT = 'YES COMP' AND $COLUMN_DELSTAT = 'NOT DEL' ORDER BY $COLUMN_ID DESC"
        val db = this.writableDatabase
        val cursor: Cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            do {
                val task = Task()
                task.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)))
                task.setTask(cursor.getString(cursor.getColumnIndex(COLUMN_TASK)))
                task.setDetails(cursor.getString(cursor.getColumnIndex(COLUMN_DETAILS)))
                task.setDateTime(cursor.getString(cursor.getColumnIndex(COLUMN_DATETIME)))
                task.setCompStat(cursor.getString(cursor.getColumnIndex(COLUMN_COMPSTAT)))
                task.setDelStat(cursor.getString(cursor.getColumnIndex(COLUMN_DELSTAT)))
                println("SAVINGSQLTASKS getAllCompTask ID : ${task.getId()}\tCOLUMN_TASK : ${task.getTask()}\t" +
                        "COLUMN_DETAILS : ${task.getDetails()}\tCOLUMN_DATETIME : ${task.getDateTime()}\t"+
                        "COLUMN_COMPSTAT : ${task.getCompStat()}\tCOLUMN_DELSTAT : ${task.getDelStat()}")
                tasks.add(task)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return tasks
    }

    fun getAllTask(): ArrayList<Task>? {
        val tasks: ArrayList<Task> = ArrayList()
        val selectQuery = "SELECT  * FROM $TABLE_NAME ORDER BY $COLUMN_ID DESC"
        val db = this.writableDatabase
        val cursor: Cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            do {
                val task = Task()
                task.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)))
                task.setTask(cursor.getString(cursor.getColumnIndex(COLUMN_TASK)))
                task.setDetails(cursor.getString(cursor.getColumnIndex(COLUMN_DETAILS)))
                task.setDateTime(cursor.getString(cursor.getColumnIndex(COLUMN_DATETIME)))
                task.setCompStat(cursor.getString(cursor.getColumnIndex(COLUMN_COMPSTAT)))
                task.setDelStat(cursor.getString(cursor.getColumnIndex(COLUMN_DELSTAT)))
                println("SAVINGSQLTASKS getAllTask ID : ${task.getId()}\tCOLUMN_TASK : ${task.getTask()}\t" +
                        "COLUMN_DETAILS : ${task.getDetails()}\tCOLUMN_DATETIME : ${task.getDateTime()}\t"+
                        "COLUMN_COMPSTAT : ${task.getCompStat()}\tCOLUMN_DELSTAT : ${task.getDelStat()}")
                tasks.add(task)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return tasks
    }

    fun deleteTask(task: Task) {
        val db = this.writableDatabase
        println("SAVINGSQLTASKS deleteTaskbyID id : ${task.getId()}\tTask : ${task.getTask()}\tTABLE_NAME : ${TABLE_NAME}\tarrayOf : ${arrayOf(java.lang.String.valueOf(task.getId()))[0]}")
        db.delete( TABLE_NAME, "$COLUMN_ID = ?", arrayOf(java.lang.String.valueOf(task.getId())) )
        db.close()
    }

    fun deleteTaskbyDelStat() {
        val db = this.writableDatabase
        println("SAVINGSQLTASKS deleteTaskbyDelStat id : ${task.getId()}\tTask : ${task.getTask()}\tTABLE_NAME : ${TABLE_NAME}\tarrayOf : ${arrayOf(java.lang.String.valueOf(task.getId()))[0]}")
        db.delete( TABLE_NAME, "$COLUMN_DELSTAT = ?", arrayOf("YES DEL"))
        db.close()
    }

    fun deleteAll() {
        val db = this.writableDatabase
        println("SAVINGSQLTASKS deleteAll")
        db.delete(TABLE_NAME, null, null);
        db.close()
    }

}
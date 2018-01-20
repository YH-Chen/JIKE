package com.example.great.project.Database

import android.content.ContentValues
import android.database.sqlite.SQLiteOpenHelper
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.great.project.Model.TaskInfo
import com.example.great.project.R
import java.util.ArrayList
import java.util.LinkedHashMap

/**
 * Created by 61915 on 18/01/05.
 */
class TaskInfoDB(c: Context) : SQLiteOpenHelper(c, DB_NAME, null, DB_VERSION) {
    companion object {
        val DB_VERSION:Int = 1
        private val DB_NAME = "SCHOOL.db"                  // 数据库名字
        private val TABLE_NAME = "TaskInfo"

    }

    override fun onCreate(db: SQLiteDatabase?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        val CREATE_TABLE = ("create table " + TABLE_NAME
                + "(_id integer primary key AUTOINCREMENT, "
                + "taskId integer, "
                + "pusherId text, "
                + "content text);")
        db!!.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    //in: TaskInfo实体类
    //out: 插入的条目的id
    fun addTaskInfo(ti:TaskInfo):Int{
        val db = writableDatabase
        val values = ContentValues()
        values.put("taskId", ti.taskId)
        values.put("pusherId", ti.pusherId)
        values.put("content", ti.content)
        db.insert(TABLE_NAME, null, values)
        val cursor = db.rawQuery("select last_insert_rowid() from " + TABLE_NAME, null)
        var newId = -1
        if (cursor.moveToFirst()) {
            newId = cursor.getInt(0)
        }
        cursor.close()
        db.close()
        return newId
    }

    //in: void
    //out: 所有TaskInfo的ArrayList
    fun queryAll():ArrayList<TaskInfo>{
        val db = readableDatabase
        val cursor = db.query(TABLE_NAME, null, null, null, null, null, null)
        val resultList = generateTaskInfoList(cursor)
        cursor.close()
        db.close()
        return resultList
    }

    //in: 要查询的TaskInfo的id
    //out: 该id的TaskInfo的所有信息
    fun queryById(id:Int):TaskInfo{
        val db=readableDatabase
        val whereClause = "_id = ?"
        val whereArgs = arrayOf(id.toString())
        val cursor = db.query(TABLE_NAME, arrayOf("_id", "taskId", "pusherId", "content"), whereClause, whereArgs, null, null, null)
        val resultList = generateTaskInfoList(cursor)
        //db.close then the cursor is not usable
        cursor.close()
        db.close()
        return resultList[0]
    }

    //in: 要查询的发布者id
    //out: 该发布者发布的所有TaskInfo组成的ArrayList
    fun queryByPusher(pusherId:Int):ArrayList<TaskInfo> {
        val db = readableDatabase
        val whereClause = "pusherId = ?"
        val whereArgs = arrayOf(pusherId.toString())
        val cursor = db.query(TABLE_NAME, arrayOf("_id", "taskId", "pusherId", "content"), whereClause, whereArgs, null, null, null)
        val resultList = generateTaskInfoList(cursor)
        //db.close then the cursor is not usable
        cursor.close()
        db.close()
        return resultList
    }

    //in: 要查询的Task的id
    //out: 该任务下的所有Info组成的ArrayList
    fun queryByTask(taskId:Int):ArrayList<TaskInfo>{
        val db = readableDatabase
        val whereClause = "taskId = ?"
        val whereArgs = arrayOf(taskId.toString())
        val cursor = db.query(TABLE_NAME, arrayOf("_id", "taskId", "pusherId", "content"), whereClause, whereArgs, null, null, null)
        val resultList = generateTaskInfoList(cursor)
        //db.close then the cursor is not usable
        cursor.close()
        db.close()
        return resultList
    }

    //in: 要删除的Task的Id, 删除该Task下的所有Info
    //out: void
    fun deleteByTask(taskId: Int) {
        val db = writableDatabase
        val whereClause = "taskId = ?"
        val whereArgs = arrayOf(taskId.toString())
        db.delete(TABLE_NAME, whereClause, whereArgs)
        db.close()
    }

    private fun generateTaskInfoList(cursor: Cursor): ArrayList<TaskInfo> {
        val resultList = ArrayList<TaskInfo>()
        if (cursor.moveToFirst()) {
            do {
                val temp:TaskInfo = TaskInfo(
                    cursor.getInt(cursor.getColumnIndex("_id")),
                    cursor.getInt(cursor.getColumnIndex("taskId")),
                    cursor.getString(cursor.getColumnIndex("pusherId")),
                    cursor.getString(cursor.getColumnIndex("content"))
                )
                resultList.add(temp)
            } while (cursor.moveToNext())
        }
        return resultList
    }
}


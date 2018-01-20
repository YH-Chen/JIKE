package com.example.great.project.Database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.great.project.Model.Task
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by acera on 2018/1/5.
 * TODO:UNTESTED
 */

class TaskDB(context: Context?) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION)
{
    companion object
    {
        private val TASK_TABLE_NAME = "Task"
        private val REL_TABLE_NAME = "StuTaskRelation"
        private val DB_VERSION = 1
        private val DB_NAME = "SCHOOL.db"                  // 数据库名字
        private val DTF: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA)
    }

    override fun onCreate(db: SQLiteDatabase?) {}

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int)
    {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    //TODO 修改成text ，删除任务的时候级联
    //插入新任务
    fun newTask(data: Task, acceptInvitation: Boolean):Int
    {
        val db = writableDatabase

        val values = ContentValues()
        values.put("courseId", data.courseId)
        values.put("taskName", data.taskName)
        values.put("taskBrief", data.taskBrief)
        values.put("taskDDL", DTF.format(data.taskDDL))
        values.put("creatorName", data.creatorName)
        db.insert(TASK_TABLE_NAME, null, values)

        val cursor = db.rawQuery("select last_insert_rowid() from " + TASK_TABLE_NAME, null)
        var newId = -1
        if (cursor.moveToFirst()) {
            newId = cursor.getInt(0)
        }

        val value2 = ContentValues()
        value2.put("tid", newId)
        value2.put("sName", data.creatorName)
        value2.put("acceptInvitation", acceptInvitation)
        db.insert(REL_TABLE_NAME, null, value2)

        cursor.close()
        db.close()

        return newId
    }

    //根据ID更新一个任务，返回是否成功(只有创建者可以更改)
    fun updateByTaskId(data: Task, userName: String): Boolean
    {
        var success = false

        val db = writableDatabase

        val selection = "_id = ?"
        val selectionArgs = arrayOf(data.id.toString())
        val c = db.query(TASK_TABLE_NAME, null, selection, selectionArgs, null, null, null)
        if (c.moveToNext())
        {
            if (c.getString(c.getColumnIndex("creatorName")) == userName)
            {
                val values = ContentValues()
                values.put("courseId", data.courseId)
                values.put("taskName", data.taskName)
                values.put("taskBrief", data.taskBrief)
                values.put("taskDDL", DTF.format(data.taskDDL))
                values.put("creatorName", data.creatorName)
                val whereClause = "_id = ?"
                val whereArgs = arrayOf(data.id.toString())
                db.update(TASK_TABLE_NAME, values, whereClause, whereArgs)
                success = true
            }
        }
        c.close()
        db.close()
        return success
    }

    //邀请学生参加任务 返回是否成功
    fun inviteTask(taskID: Int, studentName: String): Boolean
    {
        var success = false

        val db = writableDatabase

        val selection = "_id = ?"
        val selectionArgs = arrayOf(taskID.toString())
        val c = db.query(TASK_TABLE_NAME, null, selection, selectionArgs, null, null, null)
        if (c.moveToNext())
        {
            val c1 = db.query(REL_TABLE_NAME, null, "tid = ? and sName = ?", arrayOf(taskID.toString(), studentName), null, null, null)
            if(c1.moveToNext()){
                c1.close()
                c.close()
                db.close()
                return true
            }
            val values = ContentValues()
            values.put("tid", taskID)
            values.put("sName", studentName)
            values.put("acceptInvitation", 0)
            db.insert(REL_TABLE_NAME, null, values)
            success = true
            c1.close()
        }
        c.close()
        db.close()
        return success
    }

    //加入任务
    fun joinTask(taskID: Int, sname:String):Boolean{
        var success = false

        val db = writableDatabase

        val c1 = db.query(REL_TABLE_NAME, null, "sName = ? and tid = ?", arrayOf(sname, taskID.toString()), null, null, null)
        if(c1.moveToNext()){
            val values = ContentValues()
            values.put("acceptInvitation", 1)
            db.update(REL_TABLE_NAME, values, "sName = ? and tid = ?", arrayOf(sname, taskID.toString()))
            success = true
        }else{
            val selection = "_id = ?"
            val selectionArgs = arrayOf(taskID.toString())
            val c = db.query(TASK_TABLE_NAME, null, selection, selectionArgs, null, null, null)
            if (c.moveToNext())
            {
                val values = ContentValues()
                values.put("tid", taskID)
                values.put("sName", sname)
                values.put("acceptInvitation", 1)
                db.insert(REL_TABLE_NAME, null, values)
                success = true
            }
            c.close()
        }
        c1.close()
        db.close()
        return success
    }

    //退出一个任务
    fun quitTask(taskID: Int, studentName: String)
    {
        val db = writableDatabase
        val whereClause = "tid = ? and sName = ?"
        val whereArgs = arrayOf(taskID.toString(), studentName)
        db.delete(REL_TABLE_NAME, whereClause, whereArgs)

        val selection = "tid = ?"
        val selectionArgs = arrayOf(taskID.toString())
        val c = db.query(REL_TABLE_NAME, null, selection, selectionArgs, null, null, null)

        if (!c.moveToNext())//所有参与者被删除
        {
            val deleteClause = "_id = ?"
            val deleteArgs = arrayOf(taskID.toString())
            db.delete(TASK_TABLE_NAME, deleteClause, deleteArgs)
        }

        c.close()
        db.close()
    }

    //根据名字删除任务
    fun deleteByTaskName(data: Task)
    {
        val db = writableDatabase
        val whereClause = "taskName = ?"
        val whereArgs = arrayOf(data.taskName)
        db.delete(TASK_TABLE_NAME, whereClause, whereArgs)

        //删除任务有关人员
        val deleteClause = "tid = ?"
        val deleteArgs = arrayOf(data.id.toString())
        db.delete(REL_TABLE_NAME, deleteClause, deleteArgs)

        db.close()
    }

    //根据taskID找出参与任务的学生昵称
    fun searchParticipantsByTaskID(taskID: Int): List<String>
    {
        val ans: LinkedList<String> = LinkedList()
        val db = readableDatabase
        val c = db.rawQuery("select nickname from STUDENTS, StuTaskRelation " +
            "where STUDENTS.sname = StuTaskRelation.sName and StuTaskRelation.tid = ?", arrayOf(taskID.toString()))
        if (c.moveToNext())
        {
            ans.add(c.getString(c.getColumnIndex("nickname")))
            while (c.moveToNext())
                ans.add(c.getString(c.getColumnIndex("nickname")))
        }
        c.close()
        db.close()
        return ans
    }

    //根据sname查出其参与的任务
    fun searchByParticipantName(sname:String):List<Task>
    {
        val db = readableDatabase
        val c = db.rawQuery("select _id, courseId, taskName, taskBrief, taskDDL, creatorName " +
            "from Task, StuTaskRelation where Task._id = StuTaskRelation.tid and StuTaskRelation.sname = ?", arrayOf(sname))
        val ans = cursorToList(c)
        c.close()
        db.close()
        return ans
    }

    fun searchByParticipantNameAndDDL(DDL:Date, sName:String):List<Task>{
        val db = readableDatabase
        val c = db.rawQuery("select * from " + TASK_TABLE_NAME +
                " where taskDDL = ? and _id in " +
                "(select tid from " + REL_TABLE_NAME +
                " where sName = ?)", arrayOf(DTF.format(DDL), sName))
        val ans = cursorToList(c)
        c.close()
        db.close()
        return ans
    }

    //根据课程ID查出任务
    fun searchByCourseID(cid:Int):List<Task>{
        val db = readableDatabase
        val c = db.query(TASK_TABLE_NAME, null, "courseId = ?", arrayOf(cid.toString()), null, null, null)
        val ans = cursorToList(c)
        c.close()
        db.close()
        return ans
    }

    //学生参与的任务: 0 没参加，1 被邀请，2 已参加
    fun searchByJoinType(sname: String, courseId:Int, joinType:Int):List<Task>{
        val db = readableDatabase
        val queryStatement = when (joinType) {
            0 -> "select * " +
                    "from Task " +
                    "where courseId = ? and _id not in " +
                    "(select tid from StuTaskRelation " +
                    "where sName = ?)"
            1 -> "select * "+
                    "from Task " +
                    "where courseId = ? and  _id in " +
                    "(select tid from StuTaskRelation " +
                    "where acceptInvitation = 0 and sName = ?)"
            2 -> "select * "+
                    "from Task " +
                    "where courseId = ? and _id in " +
                    "(select tid from StuTaskRelation " +
                    "where acceptInvitation = 1 and sName = ?)"
            else -> ""
        }
        val c = db.rawQuery(queryStatement, arrayOf(courseId.toString(), sname))
        val res = cursorToList(c)
        c.close()
        db.close()
        return res
    }

    //学生是否参加了任务: 0 没参加，1 被邀请，2 已参加
    fun getJoinType(sname:String, taskId:Int):Int{
        val db = readableDatabase
        var res = 0
        val c1 = db.query(REL_TABLE_NAME, null, "sName = ? and tid = ?",
                arrayOf(sname, taskId.toString()), null, null, null)
        if(c1.moveToNext()){
            res = 1
            val c2 = db.query(REL_TABLE_NAME, null, "acceptInvitation = 1 and sName = ? and tid = ?",
                    arrayOf(sname, taskId.toString()), null, null, null)
            if(c2.moveToNext()){
                res = 2
            }
            c2.close()
        }
        c1.close()
        db.close()
        return res
    }

    //根据任务名字查询任务
    fun searchByTaskName(taskName: String): List<Task>
    {
        val db = readableDatabase
        val selection = "taskName = ?"
        val selectionArgs = arrayOf(taskName)
        val c = db.query(TASK_TABLE_NAME, null, selection, selectionArgs, null, null, null)

        val ans = cursorToList(c)
        c.close()
        db.close()
        return ans
    }

    //根据 id 查询任务
    fun searchByTaskID(id: Int): Task?
    {
        var ans: Task? = null
        val db = readableDatabase
        val selection = "_id = ?"
        val selectionArgs = arrayOf(id.toString())
        val c = db.query(TASK_TABLE_NAME, null, selection, selectionArgs, null, null, null)

        if (c.moveToNext())
        {
            ans = Task(c.getInt(c.getColumnIndex("_id")),
                    c.getInt(c.getColumnIndex("courseId")),
                    c.getString(c.getColumnIndex("taskName")),
                    c.getString(c.getColumnIndex("taskBrief")),
                    DTF.parse(c.getString(c.getColumnIndex("taskDDL"))),
                    c.getString(c.getColumnIndex("creatorName")))
        }
        c.close()
        db.close()
        return ans
    }

    //根据创建者 id 获取任务
    fun searchByCreatorID(creatorName: String): List<Task>
    {
        val db = readableDatabase
        val selection = "creatorName = ?"
        val selectionArgs = arrayOf(creatorName)
        val c = db.query(TASK_TABLE_NAME, null, selection, selectionArgs, null, null, null)
        val ans = cursorToList(c)
        c.close()
        db.close()
        return ans
    }

    //获取所有任务
    fun allTasks(): List<Task>
    {
        val db = readableDatabase
        val c = db.query(TASK_TABLE_NAME, null, null, null, null, null, null)
        val ans = cursorToList(c)
        c.close()
        db.close()
        return ans
    }

    //cursor转List
    private fun cursorToList(c: Cursor):List<Task>{
        val ans = LinkedList<Task>()
        if (c.moveToNext())
        {
            ans.add(Task(c.getInt(c.getColumnIndex("_id")),
                    c.getInt(c.getColumnIndex("courseId")),
                    c.getString(c.getColumnIndex("taskName")),
                    c.getString(c.getColumnIndex("taskBrief")),
                    DTF.parse(c.getString(c.getColumnIndex("taskDDL"))),
                    c.getString(c.getColumnIndex("creatorName"))))
            while (c.moveToNext())
                ans.add(Task(c.getInt(c.getColumnIndex("_id")),
                        c.getInt(c.getColumnIndex("courseId")),
                        c.getString(c.getColumnIndex("taskName")),
                        c.getString(c.getColumnIndex("taskBrief")),
                        DTF.parse(c.getString(c.getColumnIndex("taskDDL"))),
                        c.getString(c.getColumnIndex("creatorName"))))
        }
        return ans
    }
}
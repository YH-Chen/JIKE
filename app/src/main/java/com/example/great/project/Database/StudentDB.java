package com.example.great.project.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.great.project.Model.StuSet;
import com.example.great.project.Model.Student;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Danboard on 18-1-5.
 */

public class StudentDB extends SQLiteOpenHelper {
    private static final String DB_NAME = "SCHOOL.db";                  // 数据库名字
    private static final String STU_TABLE_NAME = "STUDENTS";            // "学生"表名
    private static final String SET_TABLE_NAME = "SETTINGS";            // "设置"表名
    private static final String COURSE_TABLE_NAME = "Courses";          // "课程"表名
    private static final String COURSE_REL_TABLE_NAME = "StuCourses";   // "学生-课程"表名
    private static final String TASK_TABLE_NAME = "Task";               // "任务"表名
    private static final String TASK_REL_TABLE_NAME = "StuTaskRelation";   // "学生-任务"表名
    private static final String TI_TABLE_NAME = "TaskInfo";             // "任务信息"表名
    private static final int DB_VERSION = 1;                            // 版本号

    public StudentDB(Context context) {  // 构造函数，方便创建，只传入context
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_STU_TABLE = "create table " + STU_TABLE_NAME
                + "(sname text primary key, "
                + "nickname text not null, "
                + "password text not null, "
                + "headimage text);";
        String CREATE_SET_TABLE = "create table " + SET_TABLE_NAME
                + "(sname text primary key, "
                + "maxtask integer not null, "
                + "studytime integer not null, "
                + "resttime integer not null);";
        db.execSQL(CREATE_STU_TABLE);
        db.execSQL(CREATE_SET_TABLE);
        String CREATE_COURSE_TABLE1 = "create table " + COURSE_TABLE_NAME
                + " (courseId integer primary key autoincrement, "
                + "courseName text , "
                + "weekday integer , "
                + "startTime text , "
                + "endTime text , "
                + "teacherName text, "
                + "room text);";
        String CREATE_COURSE_TABLE2 = "create table " + COURSE_REL_TABLE_NAME
                + " (Id integer primary key autoincrement, "
                + "sname text , "
                + "cid integer)";
        db.execSQL(CREATE_COURSE_TABLE1);
        db.execSQL(CREATE_COURSE_TABLE2);
        String CREATE_TI_TABLE = "create table " + TI_TABLE_NAME
                + "(_id integer primary key AUTOINCREMENT, "
                + "taskId integer, "
                + "pusherId text, "
                + "content text);";
        db.execSQL(CREATE_TI_TABLE);
        String CREATE_TASK_TABLE = "create table " + TASK_TABLE_NAME +
                " (_id integer primary key autoincrement, " +
                "courseId integer not null, " +
                "taskName text not null, " +
                "taskBrief text, " +
                "taskDDL text, " +
                "creatorName text);";
        String CREATE_TASK_REL_TABLE = "create table " + TASK_REL_TABLE_NAME +
                "(tid integer not null," +
                "sName text not null," +
                "acceptInvitation integer," +
                "primary key(tid,sName));";
        db.execSQL(CREATE_TASK_TABLE);
        db.execSQL(CREATE_TASK_REL_TABLE);
    }

    /* DB_VERSION 变化时调用此函数 */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + STU_TABLE_NAME);
        db.execSQL("drop table if exists " + SET_TABLE_NAME);
        onCreate(db);
    }

    /* 注册（添加）用户 */
    public void insertStu(Student item) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values1 = new ContentValues();

        values1.put("sname", item.getSName());
        values1.put("nickname", item.getNickName());
        values1.put("password", item.getPassword());
        values1.put("headimage", item.getHeadImage());

        ContentValues values2 = new ContentValues();
        values2.put("sname", item.getSName());
        values2.put("maxtask", 5);
        values2.put("studytime", 30);
        values2.put("resttime", 10);

        db.insert(STU_TABLE_NAME, null, values1);
        db.insert(SET_TABLE_NAME, null, values2);
        Log.d("TAG", "Student Insert Successfully");
    }

    /* 查询sName | 所有用户 */
    public List<Student> queryStu(String sName) {
        SQLiteDatabase db = getReadableDatabase();
        String selection;
        String[] selectionArgs;
        if(sName == null){
            selection = null;
            selectionArgs = null;
        }else {
            selection = "sname = ? ";
            selectionArgs = new String[]{sName};
        }
        Cursor c = db.query(STU_TABLE_NAME, null, selection, selectionArgs, null, null, null);
        List<Student> list = new ArrayList<>();
        while (c.moveToNext()) {
            Student item = new Student();
            item.setSName(c.getString(c.getColumnIndex("sname")));
            item.setNickName(c.getString(c.getColumnIndex("nickname")));
            item.setPassWord(c.getString(c.getColumnIndex("password")));
            item.setHeadImage(c.getString(c.getColumnIndex("headimage")));
            list.add(item);
        }
        c.close();
        Log.d("TAG", "Student Query Successfully");
        return list;
    }

    /* 修改密码 | 昵称 */
    public void updateStu(Student item) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        String whereClause = "sname = ?";
        String[] whereArgs = {item.getSName()};

        values.put("sname", item.getSName());
        values.put("nickname", item.getNickName());
        values.put("password", item.getPassword());
        values.put("headimage", item.getHeadImage());

        db.update(STU_TABLE_NAME, values, whereClause, whereArgs);
        Log.d("TAG", "Student Update Successfully");
    }

    /* 返回学习设置 */
    public StuSet QuerySetting(String sName) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(SET_TABLE_NAME, null, "sname = ?", new String[]{sName}, null, null, null);
        StuSet item = new StuSet(sName);
        while (c.moveToNext()) {
            item.setMaxTask(c.getInt(c.getColumnIndex("maxtask")));
            item.setStudyTime(c.getInt(c.getColumnIndex("studytime")));
            item.setRestTime(c.getInt(c.getColumnIndex("resttime")));
        }
        Log.d("TAG", "QuerySetting Successfully");
        c.close();
        return item;
    }

    /* 修改学习设置 */
    public void StudySetting(StuSet item) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        String whereClause = "sname = ?";
        String[] whereArgs = {item.getSName()};

        values.put("maxtask", item.getMaxTask());
        values.put("studytime", item.getStudyTime());
        values.put("resttime", item.getRestTime());

        db.update(SET_TABLE_NAME, values, whereClause, whereArgs);
        Log.d("TAG", "StudySet Update Successfully");
    }
}

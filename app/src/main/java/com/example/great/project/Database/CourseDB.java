package com.example.great.project.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.great.project.Model.CourseModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by great on 2018/1/5.
 */

public class CourseDB extends SQLiteOpenHelper {
    private static final String DB_NAME = "SCHOOL.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME1 = "Courses";
    private static final String TABLE_NAME2 = "StuCourses";

    public CourseDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){}

    public int existCourse(CourseModel course){
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME1, new String[]{"courseId"},
                "courseName = ? and weekday = ? and startTime = ? and endTime = ? and teacherName = ? and room = ?",
                new String[]{course.getCourseName(), course.getWeekDay(), course.getStartTime(), course.getEndTime(), course.getTeacherName(), course.getRoom()},
                null, null,null);
        int i = cursor.getCount();
        if(cursor.getCount() <= 0) {
            return -1;
        }
        else {
            cursor.moveToFirst();
            return cursor.getInt(cursor.getColumnIndex("courseId"));
        }
    }


    //根据学生的名字，返回该学生参与的所有课程
    public List<CourseModel> queryCourseBySname(String sname){
        SQLiteDatabase db = getWritableDatabase();
        List<CourseModel> queryRes = new ArrayList<>();
        Cursor cursor = db.rawQuery("select courseId, courseName, weekday, startTime, endTime, teacherName, room " +
                "from Courses, StuCourses " +
                "where Courses.courseId = StuCourses.cid and StuCourses.sname = ?", new String[]{sname});
        if (cursor.getCount() <= 0) return queryRes;
        while (cursor.moveToNext()){
            CourseModel tmp = new CourseModel();
            tmp.setCourseId(cursor.getInt(cursor.getColumnIndex("courseId")));
            tmp.setCourseName(cursor.getString(cursor.getColumnIndex("courseName")));
            tmp.setWeekDay(cursor.getString(cursor.getColumnIndex("weekday")));
            tmp.setStartTime(cursor.getString(cursor.getColumnIndex("startTime")));
            tmp.setEndTime(cursor.getString(cursor.getColumnIndex("endTime")));
            tmp.setTeacherName(cursor.getString(cursor.getColumnIndex("teacherName")));
            tmp.setRoom(cursor.getString(cursor.getColumnIndex("room")));
            queryRes.add(tmp);
        }
        cursor.close();
        db.close();
        return queryRes;
    }

    //返回所有课程
    public List<CourseModel> getAllCourses(){
        SQLiteDatabase db = getWritableDatabase();
        ArrayList<CourseModel> queryRes = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from Courses", null);
        if(cursor.getCount() <= 0) return queryRes;
        while (cursor.moveToNext()){
            CourseModel tmp = new CourseModel();
            tmp.setCourseId(cursor.getInt(cursor.getColumnIndex("courseId")));
            tmp.setCourseName(cursor.getString(cursor.getColumnIndex("courseName")));
            tmp.setWeekDay(cursor.getString(cursor.getColumnIndex("weekday")));
            tmp.setStartTime(cursor.getString(cursor.getColumnIndex("startTime")));
            tmp.setEndTime(cursor.getString(cursor.getColumnIndex("endTime")));
            tmp.setTeacherName(cursor.getString(cursor.getColumnIndex("teacherName")));
            tmp.setRoom(cursor.getString(cursor.getColumnIndex("room")));
            queryRes.add(tmp);
        }
        cursor.close();
        db.close();
        return queryRes;
    }

    //传入参数为课程id和学生名称，用于学生从已存在课程列表选择课程加入
    public void addExistedCourse(int courseId, String sname){
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = "cid = ? and sname = ?";
        Cursor cursor = db.query(TABLE_NAME2, null,
                whereClause , new String[]{Integer.toString(courseId), sname},
                null, null, null);
        if(cursor.getCount() > 0) {
            cursor.close();
            db.close();
            return;
        }
        ContentValues values = new ContentValues();
        //values.put("Id", null);
        values.put("sname", sname);
        values.put("cid", courseId);
        db.insert("StuCourses", null, values);
        cursor.close();
        db.close();
    }

    //用于添加新课程，传入执行添加操作的学生名和课程的model类。
    public void addNewCourse(String sname, CourseModel course) {
        SQLiteDatabase db = getWritableDatabase();
        if(existCourse(course) > 0){
            addExistedCourse(existCourse(course), sname);
        }
        else{
            ContentValues values = new ContentValues();
            //values.put("Id", null);

            values.put("courseName", course.getCourseName());
            values.put("weekday", course.getWeekDay());
            values.put("startTime", course.getStartTime());
            values.put("endTime", course.getEndTime());
            values.put("teacherName", course.getTeacherName());
            values.put("room", course.getRoom());
            db = getWritableDatabase();
            db.insert("Courses", null, values);
            Cursor cursor = db.rawQuery("select last_insert_rowid() from Courses",null);
            if(cursor.moveToFirst())
            {
                int id = cursor.getInt(0);
                addExistedCourse(id, sname);
            }

            cursor.close();
        }
        db.close();
    }

    //用于修改课程内容，传入执行修改的学生名和课程的model类
    public void updateCourse(String sname, CourseModel course){
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME2, null,
                "cid = ?", new String[]{Integer.toString(course.getCourseId())},
                null, null, null);
        if(cursor.getCount() == 1){
            String whereClause = "courseId = ?";
            String[] whereArgs = {Integer.toString(course.getCourseId())};
            db.delete("Courses", whereClause, whereArgs);
        }
        String whereClause = "cid = ?";
        String[] whereArgs = {Integer.toString(course.getCourseId())};
        db.delete("StuCourses", whereClause, whereArgs);
        addNewCourse(sname, course);
        cursor.close();
        db.close();
    }

    //用于删除课程，传入执行删除的学生名和要删除的课程id
    public void deleteCourse(String sname, int cid){
        SQLiteDatabase db = getWritableDatabase();
        String idStr = Integer.toString(cid);
        Cursor cursor = db.query(TABLE_NAME2, null, "cid = ?",
                new String[]{idStr}, null, null, null);
        if(cursor.getCount() == 1){
            String whereClause = "courseId = ?";
            String[] whereArgs = {Integer.toString(cid)};
            db.delete(TABLE_NAME1, whereClause, whereArgs);
        }
        String whereClause = "cid = ?";
        String[] whereArgs = {Integer.toString(cid)};
        db.delete(TABLE_NAME2, whereClause, whereArgs);
        cursor.close();
        db.close();
    }

}

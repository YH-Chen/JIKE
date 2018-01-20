package com.example.great.project.Model;

import java.io.Serializable;

/**
 * Created by great on 2018/1/5.
 */

public class CourseModel implements Serializable {
    private int courseId;
    private String courseName;
    private String weekDay;
    private String startTime;
    private String endTime;
    private String teacherName;
    private String room;
    /*CourseModel(String coursename, int weekday, String starttime, String endtime, String teacherame){
        courseName = coursename;
        weekDay = weekday;
        startTime = starttime;
        endTime = endtime;
        teacherName = teacherame;
    }*/

    //CourseModel(){}

    public String getWeekDay() {
        return weekDay;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public int getCourseId() {
        return courseId;
    }

    public String getRoom() {
        return room;
    }

    public String getTime(){
        String time = weekDay;
        time += startTime;
        time += " - ";
        time += endTime;
        return time;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public void setWeekDay(String weekDay) {
        this.weekDay = weekDay;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public void setRoom(String room) {
        this.room = room;
    }
}

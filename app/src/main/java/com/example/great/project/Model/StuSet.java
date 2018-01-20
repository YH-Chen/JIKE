package com.example.great.project.Model;

/**
 * Created by Danboard on 18-1-6.
 */

public class StuSet {
    private String sName;
    private Integer maxTask;
    private Integer studyTime;
    private Integer restTime;

    public StuSet(){

    }
    public StuSet(String sName){
        this.sName = sName;
        this.maxTask = 5;
        this.studyTime = 30;
        this.restTime = 10;
    }
    public StuSet(String sName, Integer maxTask, Integer studyTime, Integer restTime){
        this.sName = sName;
        this.maxTask = maxTask;
        this.studyTime = studyTime;
        this.restTime = restTime;
    }
    public void setMaxTask(Integer maxTask) {
        this.maxTask = maxTask;
    }
    public void setStudyTime(Integer startTime) {
        this.studyTime = startTime;
    }
    public void setRestTime(Integer restTime) {
        this.restTime = restTime;
    }

    public String getSName() {return sName;}
    public Integer getMaxTask() {
        return maxTask;
    }
    public Integer getStudyTime() {
        return studyTime;
    }
    public Integer getRestTime() {
        return restTime;
    }
}
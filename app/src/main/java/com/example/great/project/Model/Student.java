package com.example.great.project.Model;

/**
 * Created by Danboard on 18-1-5.
 */

public class Student {
    private String sName;
    private String nickName;
    private String password;
    private String headImage;

    public Student(){}
    public Student(String sName, String nickName, String password) {
        this.sName = sName;
        this.nickName = nickName;
        this.password = password;
    }
    public Student(String sName, String nickName, String password, String headImage) {
        this.sName = sName;
        this.nickName = nickName;
        this.password = password;
        this.headImage = headImage;
    }

    public void setSName(String sName) {
        this.sName = sName;
    }
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
    public void setPassWord(String password) {
        this.password = password;
    }
    public void setHeadImage(String headImage) {
        this.headImage = headImage;
    }

    public String getSName() {return sName;}
    public String getNickName() {
        return nickName;
    }
    public String getPassword() {
        return password;
    }
    public String getHeadImage() {
        return headImage;
    }
}

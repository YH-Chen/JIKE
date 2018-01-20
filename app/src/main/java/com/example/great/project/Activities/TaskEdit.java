package com.example.great.project.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.great.project.R;

public class TaskEdit extends AppCompatActivity {

    //任务编辑后不直接修改任务表中相应项
    //如果有修改，则新建项，同时从任务-user表中删除相应条目。
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_edit);
    }
}

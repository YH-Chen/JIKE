package com.example.great.project.Model

import java.util.*

/**
 * Created by acera on 2018/1/5.
 * fuck comment
 */

data class Task(
        val id: Int,
        val courseId: Int,
        val taskName: String,
        val taskBrief: String,
        val taskDDL: Date,
        val creatorName: String)
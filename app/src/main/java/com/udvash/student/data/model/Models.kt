package com.udvash.student.data.model

data class ClassItem(
    val title: String,
    val date: String,
    val subject: String,
    val videoUrl: String?,
    val lectureNoteUrl: String?,
    val practiceNoteUrl: String?
)

data class UserProfile(
    val name: String,
    val regNo: String
)

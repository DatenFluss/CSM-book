package com.android.csm_book.presentation.content

import android.content.Context
import com.google.gson.Gson
import java.io.InputStreamReader

fun loadStudyPageContent(context: Context, resId: Int): StudyPageContent {
    val inputStream = context.resources.openRawResource(resId)
    val reader = InputStreamReader(inputStream)
    return Gson().fromJson(reader, StudyPageContent::class.java)
}

fun loadTestPageContent(context: Context, resId: Int): TestPageContent {
    val inputStream = context.resources.openRawResource(resId)
    val reader = InputStreamReader(inputStream)
    return Gson().fromJson(reader, TestPageContent::class.java)
}

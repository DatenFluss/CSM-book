package com.android.csm_book.presentation.content

import android.content.Context
import com.google.gson.Gson
import java.io.InputStreamReader

fun loadStudyPageContent(context: Context, resId: Int): StudyPageContent {
    return Gson().fromJson(getInputStreamReader(context, resId), StudyPageContent::class.java)
}

fun loadTestPageContent(context: Context, resId: Int): TestPageContent {
    return Gson().fromJson(getInputStreamReader(context, resId), TestPageContent::class.java)
}

fun getInputStreamReader(context: Context, resId: Int): InputStreamReader {
    val inputStream = context.resources.openRawResource(resId)
    return InputStreamReader(inputStream)
}

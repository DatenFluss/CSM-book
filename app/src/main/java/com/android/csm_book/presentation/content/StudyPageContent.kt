package com.android.csm_book.presentation.content

data class StudyPageContent(
    val title: String,
    val theoreticalInformation: String,
    val mathFormulas: String,
    val codeSnippets: List<String>,
    val visualizationLabel: String,
    val testLabel: String
)

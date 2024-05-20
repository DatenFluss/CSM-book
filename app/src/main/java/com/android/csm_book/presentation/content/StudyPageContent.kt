package com.android.csm_book.presentation.content

data class StudyPageContent(
    val title: String,
    val theoreticalInformation: List<String>,
    val mathFormulas: List<String>,
    val codeSnippets: List<CodeSnippet>,
    val visualizationLabel: String,
    val testLabel: String
)

data class CodeSnippet(
    val title: String,
    val code: String
)

package com.android.csm_book.presentation.content

data class TestPageContent(
    val title: String,
    val questions: List<Question>
)

data class Question(
    val question: String,
    val answers: List<String>,
    val correctAnswer: String
)
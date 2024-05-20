package com.android.csm_book.presentation.content.sorting_algorithms

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.android.csm_book.R
import com.android.csm_book.presentation.content.BaseTestPage
import com.android.csm_book.presentation.content.loadTestPageContent

@Composable
fun SortingAlgorithmsTestPage(navController: NavHostController) {
    val context = LocalContext.current
    val testPageContent = remember { loadTestPageContent(context, R.raw.sorting_algorithms_test) }

    BaseTestPage(
        navController = navController,
        title = testPageContent.title,
        questions = testPageContent.questions
    )
}

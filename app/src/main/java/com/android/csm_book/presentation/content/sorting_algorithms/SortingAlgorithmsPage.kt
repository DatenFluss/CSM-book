package com.android.csm_book.presentation.content.sorting_algorithms

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.android.csm_book.R
import com.android.csm_book.presentation.content.BaseStudyPage
import com.android.csm_book.presentation.content.loadStudyPageContent

@Composable
fun SortingAlgorithmsPage(navController: NavHostController) {
    val context = LocalContext.current
    val studyPageContent = remember { loadStudyPageContent(context, R.raw.sorting_algorithms_page) }

    BaseStudyPage(
        navController = navController,
        title = studyPageContent.title,
        theoreticalInformation = studyPageContent.theoreticalInformation,
        mathFormulas = studyPageContent.mathFormulas,
        codeSnippets = studyPageContent.codeSnippets,
        visualizationLabel = studyPageContent.visualizationLabel,
        testLabel = studyPageContent.testLabel,
        visualizationRoute = "sortingAlgorithmsVisualization",
        testRoute = "sortingAlgorithmsTest"
    )
}

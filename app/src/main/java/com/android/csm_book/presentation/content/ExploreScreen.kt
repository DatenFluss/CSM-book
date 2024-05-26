package com.android.csm_book.presentation.content

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.android.csm_book.presentation.content.graphs.GraphTraversalsVisualizationPage
import com.android.csm_book.presentation.content.graphs.GraphsPage
import com.android.csm_book.presentation.content.graphs.GraphsTestPage
import com.android.csm_book.presentation.content.recursion.RecursionPage
import com.android.csm_book.presentation.content.recursion.RecursionTestPage
import com.android.csm_book.presentation.content.recursion.TowersOfHanoiVisualizationPage
import com.android.csm_book.presentation.content.sorting_algorithms.SortingAlgorithmsPage
import com.android.csm_book.presentation.content.sorting_algorithms.SortingAlgorithmsTestPage
import com.android.csm_book.presentation.content.sorting_algorithms.SortingAlgorithmsVisualizationPage
import com.android.csm_book.presentation.content.trees.TreeTraversalsVisualizationPage
import com.android.csm_book.presentation.content.trees.TreesPage
import com.android.csm_book.presentation.content.trees.TreesTestPage

@Composable
fun ExploreScreen(
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ExploreNavController()
    }
}

@Composable
fun ExploreNavController() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "topicList") {
        composable("topicList") { TopicListScreen(navController) }

        // theoretical material pages
        composable("graphAlgorithms") { RecursionPage(navController) }
        composable("recursion") { RecursionPage(navController) }
        composable("sortingAlgorithms") { SortingAlgorithmsPage(navController) }
        composable("trees") { TreesPage(navController) }
        composable("graphs") { GraphsPage(navController) }

        // visualization pages
        composable("towersOfHanoiVisualization") { TowersOfHanoiVisualizationPage(navController) }
        composable("sortingAlgorithmsVisualization") { SortingAlgorithmsVisualizationPage(navController) }
        composable("treesVisualization") { TreeTraversalsVisualizationPage(navController) }
        composable("graphsVisualization") { GraphTraversalsVisualizationPage(navController) }

        // test pages
        composable("recursionTest") { RecursionTestPage(navController) }
        composable("sortingAlgorithmsTest") { SortingAlgorithmsTestPage(navController) }
        composable("treesTest") { TreesTestPage(navController) }
        composable("graphsTest") { GraphsTestPage(navController) }
    }
}

@Composable
fun TopicListScreen(navController: NavHostController) {
    val topics = listOf(
        "Sorting Algorithms" to "sortingAlgorithms",
        "Graphs" to "graphs",
        "Recursion" to "recursion",
        "Dynamic Programming" to "dynamicProgramming",
        "Trees" to "trees",
        // Add more topics here
    )

    Column(modifier = Modifier.padding(16.dp)) {
        topics.forEach { (topic, route) ->
            TopicItem(
                topic = topic,
                onClick = { navController.navigate(route) }
            )
        }
    }
}

@Composable
fun TopicItem(topic: String, onClick: () -> Unit) {
    Text(
        text = topic,
        fontSize = 20.sp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp)
    )
}

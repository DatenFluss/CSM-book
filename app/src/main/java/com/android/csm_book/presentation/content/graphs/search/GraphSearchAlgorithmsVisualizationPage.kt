package com.android.csm_book.presentation.content.graphs.search

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GraphVisualizationPage(navController: NavHostController, viewModel: GraphAsGridViewModel = viewModel()) {
    val grid by viewModel.grid.collectAsState()
    val startNode by viewModel.startNode.collectAsState()
    val endNode by viewModel.endNode.collectAsState()
    val searchPath by viewModel.searchPath.collectAsState()
    val visitedNodes by viewModel.visitedNodes.collectAsState()
    val walls by viewModel.walls.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Graph Search Algorithms Visualization") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Grid(grid, startNode, endNode, searchPath, visitedNodes, walls, viewModel)
            Spacer(modifier = Modifier.height(16.dp))
            ControlButtons(viewModel)
        }
    }
}

@Composable
fun Grid(
    grid: Grid,
    startNode: Node?,
    endNode: Node?,
    searchPath: List<Node>,
    visitedNodes: List<Node>,
    walls: Set<Node>,
    viewModel: GraphAsGridViewModel
) {
    Column {
        for (row in grid.nodes) {
            Row {
                for (node in row) {
                    NodeTile(node, startNode, endNode, searchPath, visitedNodes, walls, viewModel)
                }
            }
        }
    }
}

@Composable
fun NodeTile(
    node: Node,
    startNode: Node?,
    endNode: Node?,
    searchPath: List<Node>,
    visitedNodes: List<Node>,
    walls: Set<Node>,
    viewModel: GraphAsGridViewModel
) {
    val color = when {
        node.isStart -> Color.Green
        node.isEnd -> Color.Red
        node.isWall -> Color.Black
        node in searchPath -> Color.Yellow
        node in visitedNodes -> Color.Blue
        else -> Color.White
    }
    Box(
        modifier = Modifier
            .size(24.dp)
            .background(color, RoundedCornerShape(4.dp))
            .clickable {
                when {
                    startNode == null -> viewModel.setStartNode(node)
                    endNode == null -> viewModel.setEndNode(node)
                    else -> viewModel.toggleWall(node)
                }
            }
            .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
    )
}

@Composable
fun ControlButtons(viewModel: GraphAsGridViewModel) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Button(onClick = {
            viewModel.runDijkstra()
            keyboardController?.hide()
            hideKeyboard(context)
        }) {
            Text("Run Dijkstra")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            viewModel.runAStar()
            keyboardController?.hide()
            hideKeyboard(context)
        }) {
            Text("Run A*")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            viewModel.resetGrid()
            keyboardController?.hide()
            hideKeyboard(context)
        }) {
            Text("Reset")
        }
    }
}

fun hideKeyboard(context: Context) {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow((context as android.app.Activity).currentFocus?.windowToken, 0)
}

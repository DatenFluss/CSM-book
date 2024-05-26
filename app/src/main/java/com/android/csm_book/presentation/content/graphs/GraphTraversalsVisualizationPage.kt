package com.android.csm_book.presentation.content.graphs

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GraphTraversalsVisualizationPage(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Graph Traversals Visualization") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            GraphTraversalsVisualization()
        }
    }
}

@Composable
fun GraphTraversalsVisualization() {
    var selectedTraversal by remember { mutableStateOf("BFS") }
    val traversals = listOf("BFS", "DFS")
    val graph = remember { generateSampleGraph() }
    val coroutineScope = rememberCoroutineScope()
    var isTraversing by remember { mutableStateOf(false) }
    var traversalResult by remember { mutableStateOf<List<Int>>(emptyList()) }
    var highlightedNodes by remember { mutableStateOf<List<Int>>(emptyList()) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Text("Select Traversal Type", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(16.dp))

        DropdownMenu(selectedTraversal, traversals) { traversal ->
            selectedTraversal = traversal
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    isTraversing = true
                    traversalResult = emptyList()
                    highlightedNodes = emptyList()
                    coroutineScope.launch {
                        when (selectedTraversal) {
                            "BFS" -> bfs(graph, 1) { highlightedNodes = it }
                            "DFS" -> dfs(graph, 1, highlightedNodes.toMutableSet()) { highlightedNodes = it }
                        }
                        isTraversing = false
                    }
                },
                enabled = !isTraversing && highlightedNodes.isEmpty(),
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Start")
            }

            Button(
                onClick = {
                    highlightedNodes = emptyList()
                },
                enabled = !isTraversing && highlightedNodes.isNotEmpty(),
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Reset")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        GraphCanvas(graph, highlightedNodes)
    }
}

@Composable
fun DropdownMenu(selectedTraversal: String, traversals: List<String>, onSelectTraversal: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        TextButton(onClick = { expanded = !expanded }) {
            Text(selectedTraversal, style = MaterialTheme.typography.bodyLarge)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            traversals.forEach { traversal ->
                DropdownMenuItem(
                    text = { Text(traversal) },
                    onClick = {
                        onSelectTraversal(traversal)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun GraphCanvas(graph: Graph, highlightedNodes: List<Int>) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawGraph(graph, highlightedNodes)
    }
}

fun DrawScope.drawGraph(graph: Graph, highlightedNodes: List<Int>) {
    val nodeRadius = 20f
    val positions = calculateNodePositions(graph)

    graph.adjacencyList.forEach { (node, neighbors) ->
        val nodePosition = positions[node]!!
        neighbors.forEach { neighbor ->
            val neighborPosition = positions[neighbor]!!
            drawLine(
                color = Color.Black,
                start = nodePosition,
                end = neighborPosition
            )
        }
    }

    positions.forEach { (node, position) ->
        drawCircle(
            color = if (node in highlightedNodes) Color.Red else Color.Blue,
            radius = nodeRadius,
            center = position
        )
        drawContext.canvas.nativeCanvas.drawText(node.toString(), position.x, position.y - 30f, androidx.compose.ui.graphics.Paint().asFrameworkPaint().apply { textSize = 30f })
    }
}

fun calculateNodePositions(graph: Graph): Map<Int, Offset> {
    // Generate positions for nodes in a circular layout
    val positions = mutableMapOf<Int, Offset>()
    val center = Offset(400f, 400f)
    val radius = 300f
    val angleIncrement = 2 * Math.PI / graph.adjacencyList.size
    var currentAngle = 0.0

    graph.adjacencyList.keys.forEach { node ->
        val x = center.x + radius * Math.cos(currentAngle).toFloat()
        val y = center.y + radius * Math.sin(currentAngle).toFloat()
        positions[node] = Offset(x, y)
        currentAngle += angleIncrement
    }

    return positions
}

data class Graph(val adjacencyList: MutableMap<Int, MutableList<Int>>)

fun generateSampleGraph(): Graph {
    val graph = Graph(mutableMapOf())
    graph.addEdge(1, 2)
    graph.addEdge(1, 3)
    graph.addEdge(2, 4)
    graph.addEdge(2, 5)
    graph.addEdge(3, 6)
    graph.addEdge(3, 7)
    return graph
}

fun Graph.addEdge(source: Int, destination: Int) {
    adjacencyList.computeIfAbsent(source) { mutableListOf() }.add(destination)
    adjacencyList.computeIfAbsent(destination) { mutableListOf() }.add(source)
}

suspend fun bfs(graph: Graph, start: Int, onTraversalUpdate: (List<Int>) -> Unit) {
    val result = mutableListOf<Int>()
    val visited = mutableSetOf<Int>()
    val queue = ArrayDeque<Int>()
    visited.add(start)
    queue.add(start)

    while (queue.isNotEmpty()) {
        val vertex = queue.removeFirst()
        result.add(vertex)
        onTraversalUpdate(result.toList())
        delay(500) // Delay for visualization
        for (neighbor in graph.adjacencyList[vertex]!!) {
            if (neighbor !in visited) {
                visited.add(neighbor)
                queue.add(neighbor)
            }
        }
    }
}

suspend fun dfs(graph: Graph, start: Int, visited: MutableSet<Int>, onTraversalUpdate: (List<Int>) -> Unit) {
    if (start !in visited) {
        visited.add(start)
        onTraversalUpdate(visited.toList())
        delay(500) // Delay for visualization
        for (neighbor in graph.adjacencyList[start]!!) {
            dfs(graph, neighbor, visited, onTraversalUpdate)
        }
    }
}


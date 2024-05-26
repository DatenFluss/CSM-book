package com.android.csm_book.presentation.content.trees

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
fun TreeTraversalsVisualizationPage(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tree Traversals Visualization") },
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
            TreeTraversalsVisualization()
        }
    }
}

@Composable
fun TreeTraversalsVisualization() {
    var selectedTraversal by remember { mutableStateOf("Inorder") }
    val traversals = listOf("Inorder", "Preorder", "Postorder", "Level Order")
    val tree = remember { generateSampleTree() }
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
                            "Inorder" -> inorderTraversal(tree) { highlightedNodes = it }
                            "Preorder" -> preorderTraversal(tree) { highlightedNodes = it }
                            "Postorder" -> postorderTraversal(tree) { highlightedNodes = it }
                            "Level Order" -> levelOrderTraversal(tree) { highlightedNodes = it }
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

        TreeCanvas(tree, highlightedNodes)
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
fun TreeCanvas(tree: TreeNode?, highlightedNodes: List<Int>) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        if (tree != null) {
            drawTree(tree, highlightedNodes)
        }
    }
}

fun DrawScope.drawTree(node: TreeNode, highlightedNodes: List<Int>, x: Float = size.width / 2, y: Float = 50f, xOffset: Float = size.width / 4) {
    drawCircle(
        color = if (node.value in highlightedNodes) Color.Red else Color.Blue,
        radius = 20f,
        center = Offset(x, y)
    )
    drawContext.canvas.nativeCanvas.drawText(node.value.toString(), x, y - 30f, androidx.compose.ui.graphics.Paint().asFrameworkPaint().apply { textSize = 30f })

    node.left?.let {
        drawLine(
            color = Color.Black,
            start = Offset(x, y + 20f),
            end = Offset(x - xOffset, y + 100f)
        )
        drawTree(it, highlightedNodes, x - xOffset, y + 100f, xOffset / 2)
    }

    node.right?.let {
        drawLine(
            color = Color.Black,
            start = Offset(x, y + 20f),
            end = Offset(x + xOffset, y + 100f)
        )
        drawTree(it, highlightedNodes, x + xOffset, y + 100f, xOffset / 2)
    }
}

data class TreeNode(val value: Int, val left: TreeNode? = null, val right: TreeNode? = null)

fun generateSampleTree(): TreeNode {
    return TreeNode(
        value = 1,
        left = TreeNode(
            value = 2,
            left = TreeNode(4),
            right = TreeNode(5)
        ),
        right = TreeNode(
            value = 3,
            left = TreeNode(6),
            right = TreeNode(7)
        )
    )
}

suspend fun inorderTraversal(node: TreeNode?, onTraversalUpdate: (List<Int>) -> Unit) {
    val result = mutableListOf<Int>()
    suspend fun inorder(node: TreeNode?) {
        if (node == null) return
        inorder(node.left)
        result.add(node.value)
        onTraversalUpdate(result.toList())
        delay(500) // Delay for visualization
        inorder(node.right)
    }
    inorder(node)
}

suspend fun preorderTraversal(node: TreeNode?, onTraversalUpdate: (List<Int>) -> Unit) {
    val result = mutableListOf<Int>()
    suspend fun preorder(node: TreeNode?) {
        if (node == null) return
        result.add(node.value)
        onTraversalUpdate(result.toList())
        delay(500) // Delay for visualization
        preorder(node.left)
        preorder(node.right)
    }
    preorder(node)
}

suspend fun postorderTraversal(node: TreeNode?, onTraversalUpdate: (List<Int>) -> Unit) {
    val result = mutableListOf<Int>()
    suspend fun postorder(node: TreeNode?) {
        if (node == null) return
        postorder(node.left)
        postorder(node.right)
        result.add(node.value)
        onTraversalUpdate(result.toList())
        delay(500) // Delay for visualization
    }
    postorder(node)
}

suspend fun levelOrderTraversal(node: TreeNode?, onTraversalUpdate: (List<Int>) -> Unit) {
    val result = mutableListOf<Int>()
    val queue = ArrayDeque<TreeNode?>()
    queue.add(node)
    while (queue.isNotEmpty()) {
        val currentNode = queue.removeFirst()
        if (currentNode != null) {
            result.add(currentNode.value)
            onTraversalUpdate(result.toList())
            delay(500) // Delay for visualization
            queue.add(currentNode.left)
            queue.add(currentNode.right)
        }
    }
}

package com.android.csm_book.presentation.content.trees

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
    val heights = listOf(3, 4, 5)
    var selectedHeight by remember { mutableIntStateOf(3) }
    var tree by remember { mutableStateOf(generateSampleTree(selectedHeight)) }
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

        Text("Select Tree Height", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(16.dp))

        DropdownMenu(selectedHeight.toString(), heights.map { it.toString() }) { height ->
            selectedHeight = height.toInt()
            tree = generateSampleTree(selectedHeight)
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
fun DropdownMenu(selectedItem: String, items: List<String>, onSelectItem: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        TextButton(onClick = { expanded = !expanded }) {
            Text(selectedItem, style = MaterialTheme.typography.bodyLarge)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item) },
                    onClick = {
                        onSelectItem(item)
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

data class TreeNode(val value: Int, var left: TreeNode? = null, var right: TreeNode? = null)

fun generateSampleTree(height: Int): TreeNode {
    if (height <= 0) return TreeNode(1)
    val root = TreeNode(1)
    var currentLevelNodes = mutableListOf(root)
    var currentValue = 2
    repeat(height - 1) {
        val nextLevelNodes = mutableListOf<TreeNode>()
        currentLevelNodes.forEach { node ->
            node.left = TreeNode(currentValue++)
            node.right = TreeNode(currentValue++)
            nextLevelNodes.add(node.left!!)
            nextLevelNodes.add(node.right!!)
        }
        currentLevelNodes = nextLevelNodes
    }
    return root
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

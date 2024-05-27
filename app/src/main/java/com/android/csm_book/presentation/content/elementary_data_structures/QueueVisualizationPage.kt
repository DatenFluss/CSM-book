package com.android.csm_book.presentation.content.elementary_data_structures

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueueVisualizationPage(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Queue Visualization") },
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
            QueueVisualization()
        }
    }
}

@Composable
fun QueueVisualization() {
    var queue by remember { mutableStateOf<List<Int>>(emptyList()) }
    var inputValue by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Text("Queue Visualization", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = inputValue,
            onValueChange = { inputValue = it },
            label = { Text("Value to Enqueue (1-99)") },
            isError = errorMessage != null,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)
        )
        errorMessage?.let {
            Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Button(onClick = {
                keyboardController?.hide()
                val value = inputValue.toIntOrNull()
                if (value == null || value !in 1..99) {
                    errorMessage = "Please enter a valid number between 1 and 99"
                } else if (queue.size >= 5) {
                    errorMessage = "Queue is full. Maximum size is 5"
                } else {
                    errorMessage = null
                    queue = queue + value
                    inputValue = ""
                }
            }) {
                Text("Enqueue")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                keyboardController?.hide()
                if (queue.isNotEmpty()) {
                    queue = queue.drop(1)
                } else {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Queue is empty")
                    }
                }
            }) {
                Text("Dequeue")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        QueueCanvas(queue)

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            SnackbarHost(hostState = snackbarHostState)
        }
    }
}

@Composable
fun QueueCanvas(queue: List<Int>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .border(1.dp, Color.Black)
            .padding(16.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val queueLength = 5 // Maximum size of the queue
            val boxWidth = size.width / queueLength

            queue.forEachIndexed { index, value ->
                drawRect(
                    color = Color.Green,
                    topLeft = androidx.compose.ui.geometry.Offset(boxWidth * index, 0f),
                    size = androidx.compose.ui.geometry.Size(boxWidth, size.height),
                )
                drawContext.canvas.nativeCanvas.drawText(
                    value.toString(),
                    boxWidth * index + boxWidth / 2,
                    size.height / 2 + 15f, // Adjust for better centering
                    androidx.compose.ui.graphics.Paint().asFrameworkPaint().apply {
                        textSize = 40f
                        color = android.graphics.Color.WHITE
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                )
            }
        }
    }
}

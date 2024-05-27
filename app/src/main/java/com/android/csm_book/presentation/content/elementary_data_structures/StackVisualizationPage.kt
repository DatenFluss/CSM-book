package com.android.csm_book.presentation.content.elementary_data_structures

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StackVisualizationPage(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Stack Visualization") },
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
            StackVisualization()
        }
    }
}

@Composable
fun StackVisualization() {
    var stack by remember { mutableStateOf<List<Int>>(emptyList()) }
    var inputValue by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Text("Stack Visualization", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = inputValue,
            onValueChange = { inputValue = it },
            label = { Text("Value to Push (1-99)") },
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
                } else if (stack.size >= 5) {
                    errorMessage = "Stack is full. Maximum size is 5"
                } else {
                    errorMessage = null
                    stack = stack + value
                    inputValue = ""
                }
            }) {
                Text("Push")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                keyboardController?.hide()
                if (stack.isNotEmpty()) {
                    stack = stack.dropLast(1)
                } else {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Stack is empty")
                    }
                }
            }) {
                Text("Pop")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                keyboardController?.hide()
                if (stack.isNotEmpty()) {
                    val top = stack.last()
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Top element is $top")
                    }
                } else {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Stack is empty")
                    }
                }
            }) {
                Text("Peek")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        StackCanvas(stack)

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            SnackbarHost(hostState = snackbarHostState)
        }
    }
}

@Composable
fun StackCanvas(stack: List<Int>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .border(1.dp, Color.Black)
            .padding(16.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stackHeight = 5 // Maximum size of the stack
            val boxHeight = size.height / stackHeight

            stack.forEachIndexed { index, value ->
                drawRect(
                    color = Color.Blue,
                    topLeft = androidx.compose.ui.geometry.Offset(0f, size.height - boxHeight * (index + 1)),
                    size = androidx.compose.ui.geometry.Size(size.width, boxHeight),
                )
                drawContext.canvas.nativeCanvas.drawText(
                    value.toString(),
                    size.width / 2,
                    size.height - boxHeight * (index + 1) + boxHeight / 2 + 15f, // Adjust for better centering
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

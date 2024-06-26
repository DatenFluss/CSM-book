package com.android.csm_book.presentation.content

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseTestPage(
    navController: NavHostController,
    title: String,
    questions: List<Question>
) {
    var selectedAnswers by remember { mutableStateOf(questions.map { "" }) }
    var showResults by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
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
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
        ) {
            questions.forEachIndexed { index, question ->
                Text(
                    text = "${index + 1}. ${question.question}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                question.answers.forEach { answer ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedAnswers[index] == answer,
                            onClick = {
                                selectedAnswers = selectedAnswers.toMutableList().apply { set(index, answer) }
                            }
                        )
                        Text(
                            text = answer,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            Button(
                onClick = { showResults = true },
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Text(text = "Submit")
            }
            if (showResults) {
                Text(
                    text = "Results",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                questions.forEachIndexed { index, question ->
                    val isCorrect = question.correctAnswer == selectedAnswers[index]
                    Text(
                        text = "${index + 1}. ${question.question}\nYour answer: ${selectedAnswers[index]}\nCorrect answer: ${question.correctAnswer}",
                        color = if (isCorrect) Color.Green else Color.Red,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }
        }
    }
}

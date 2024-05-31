package com.android.csm_book.presentation.content

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseStudyPage(
    navController: NavHostController,
    title: String,
    theoreticalInformation: List<String>,
    mathFormulas: List<String>,
    codeSnippets: List<CodeSnippet>,
    visualizationLabel: String,
    testLabel: String,
    visualizationRoute: String,
    testRoute: String
) {
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
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Theoretical Information",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            theoreticalInformation.forEach { info ->
                Text(
                    text = info,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Text(
                text = "Mathematical Formulas",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            mathFormulas.forEach { formula ->
                LatexView(formula, modifier = Modifier.padding(bottom = 8.dp))
            }

            Text(
                text = "Code Snippets",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            codeSnippets.forEach { snippet ->
                Column(modifier = Modifier.padding(bottom = 16.dp)) {
                    Text(text = snippet.title, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
                    Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                        Text(
                            text = snippet.code,
                            style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate(visualizationRoute) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Text(text = visualizationLabel)
            }

            Button(
                onClick = { navController.navigate(testRoute) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Text(text = testLabel)
            }
        }
    }
}
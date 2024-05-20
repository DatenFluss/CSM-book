package com.android.csm_book.presentation.content.recursion

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TowersOfHanoiVisualizationPage(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Towers of Hanoi Visualization") },
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
            TowersOfHanoi()
        }
    }
}

@Composable
fun TowersOfHanoi() {
    val towers = remember { mutableStateListOf(
        mutableStateListOf(3, 2, 1), // Tower A
        mutableStateListOf(),        // Tower B
        mutableStateListOf()         // Tower C
    ) }
    var isAnimating by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Towers of Hanoi", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(16.dp))
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            HanoiCanvas(towers)
        }
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column {
                MoveButton(from = 0, to = 1, towers = towers, label = "Move A to B", enabled = !isAnimating)
                MoveButton(from = 0, to = 2, towers = towers, label = "Move A to C", enabled = !isAnimating)
                MoveButton(from = 1, to = 0, towers = towers, label = "Move B to A", enabled = !isAnimating)
            }
            Column {
                MoveButton(from = 1, to = 2, towers = towers, label = "Move B to C", enabled = !isAnimating)
                MoveButton(from = 2, to = 0, towers = towers, label = "Move C to A", enabled = !isAnimating)
                MoveButton(from = 2, to = 1, towers = towers, label = "Move C to B", enabled = !isAnimating)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        AnimatedSolutionButton(towers, isAnimating, onAnimationStart = { isAnimating = true }, onAnimationEnd = { isAnimating = false })
    }
}

@Composable
fun HanoiCanvas(towers: List<List<Int>>) {
    val diskColors = listOf(Color.Red, Color.Green, Color.Blue)
    val towerWidth = with(LocalDensity.current) { 20.dp.toPx() }
    val diskHeight = with(LocalDensity.current) { 20.dp.toPx() }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val towerSpacing = size.width / 3

        for ((towerIndex, tower) in towers.withIndex()) {
            val towerX = towerIndex * towerSpacing + towerSpacing / 2

            // Draw the tower
            drawRect(
                color = Color.Gray,
                topLeft = Offset(towerX - towerWidth / 2, 0f),
                size = size.copy(width = towerWidth, height = size.height)
            )

            // Draw the disks
            for ((diskIndex, disk) in tower.withIndex()) {
                val diskWidth = towerWidth * (disk + 1)
                val diskX = towerX - diskWidth / 2
                val diskY = size.height - (diskIndex + 1) * diskHeight

                drawRect(
                    color = diskColors[disk % diskColors.size],
                    topLeft = Offset(diskX, diskY),
                    size = size.copy(width = diskWidth, height = diskHeight)
                )
            }
        }
    }
}

@Composable
fun MoveButton(from: Int, to: Int, towers: SnapshotStateList<SnapshotStateList<Int>>, label: String, enabled: Boolean) {
    Button(onClick = { moveDisk(towers, from, to) }, modifier = Modifier.padding(4.dp), enabled = enabled) {
        Text(text = label)
    }
}

fun moveDisk(towers: SnapshotStateList<SnapshotStateList<Int>>, from: Int, to: Int) {
    if (towers[from].isNotEmpty() && (towers[to].isEmpty() || towers[to].last() > towers[from].last())) {
        val disk = towers[from].removeLast()
        towers[to].add(disk)
    }
}

@Composable
fun AnimatedSolutionButton(
    towers: SnapshotStateList<SnapshotStateList<Int>>,
    isAnimating: Boolean,
    onAnimationStart: () -> Unit,
    onAnimationEnd: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    Button(
        onClick = {
            onAnimationStart()
            coroutineScope.launch {
                solveHanoi(towers.size, towers, 0, 2, 1)
                onAnimationEnd()
            }
        },
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        enabled = !isAnimating
    ) {
        Text(text = "Animate Solution")
    }
}

suspend fun solveHanoi(n: Int, towers: SnapshotStateList<SnapshotStateList<Int>>, from: Int, to: Int, aux: Int) {
    if (n > 0) {
        solveHanoi(n - 1, towers, from, aux, to)
        moveDisk(towers, from, to)
        delay(500) // Adjust the delay for animation speed
        solveHanoi(n - 1, towers, aux, to, from)
    }
}

package com.android.csm_book.presentation.content.sorting_algorithms

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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortingAlgorithmsVisualizationPage(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sorting Algorithms Visualization") },
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
            SortingAlgorithmsVisualization()
        }
    }
}

@Composable
fun SortingAlgorithmsVisualization() {
    var selectedAlgorithm by remember { mutableStateOf("Bubble Sort") }
    val algorithms = listOf("Bubble Sort", "Merge Sort", "Quick Sort", "Insertion Sort", "Selection Sort")
    val coroutineScope = rememberCoroutineScope()
    var isSorting by remember { mutableStateOf(false) }
    val comparedIndices = remember { mutableStateOf(Pair(-1, -1)) }

    var arraySize by remember { mutableStateOf(10f) }
    var sortSpeed by remember { mutableStateOf(300f) }
    val array = remember { mutableStateListOf<Int>() }
    LaunchedEffect(arraySize) {
        array.clear()
        repeat(arraySize.toInt()) {
            array.add(Random.nextInt(1, 100))
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Text("Select Sorting Algorithm", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(16.dp))

        DropdownMenu(selectedAlgorithm, algorithms) { algorithm ->
            selectedAlgorithm = algorithm
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text("Array Size: ${arraySize.toInt()}", style = MaterialTheme.typography.bodyLarge)
            Slider(
                value = arraySize,
                onValueChange = { arraySize = it },
                valueRange = 5f..50f,
                steps = 45,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text("Speed: ${sortSpeed.toInt()} ms", style = MaterialTheme.typography.bodyLarge)
            Slider(
                value = sortSpeed,
                onValueChange = { sortSpeed = it },
                valueRange = 100f..1000f,
                steps = 9,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    isSorting = true
                    coroutineScope.launch {
                        when (selectedAlgorithm) {
                            "Bubble Sort" -> bubbleSort(array, comparedIndices, sortSpeed.toLong()) { isSorting = false }
                            "Merge Sort" -> mergeSort(array, comparedIndices, sortSpeed.toLong()) { isSorting = false }
                            "Quick Sort" -> quickSort(array, 0, array.size - 1, comparedIndices, sortSpeed.toLong()) { isSorting = false }
                            "Insertion Sort" -> insertionSort(array, comparedIndices, sortSpeed.toLong()) { isSorting = false }
                            "Selection Sort" -> selectionSort(array, comparedIndices, sortSpeed.toLong()) { isSorting = false }
                        }
                    }
                },
                enabled = !isSorting,
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Start")
            }

            Button(
                onClick = {
                    array.shuffle()
                },
                enabled = !isSorting,
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Shuffle")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        SortingCanvas(array, comparedIndices.value)
    }
}

@Composable
fun DropdownMenu(selectedAlgorithm: String, algorithms: List<String>, onSelectAlgorithm: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        TextButton(onClick = { expanded = !expanded }) {
            Text(selectedAlgorithm, style = MaterialTheme.typography.bodyLarge)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            algorithms.forEach { algorithm ->
                DropdownMenuItem(
                    text = { Text(algorithm) },
                    onClick = {
                        onSelectAlgorithm(algorithm)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun SortingCanvas(array: List<Int>, comparedIndices: Pair<Int, Int>) {
    val barWidth = with(LocalDensity.current) { (20.dp.toPx() * 10f / array.size) }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val barSpacing = size.width / array.size

        array.forEachIndexed { index, value ->
            val barHeight = value * 10f
            val color = if (index == comparedIndices.first || index == comparedIndices.second) Color.Red else Color.Blue
            drawRect(
                color = color,
                topLeft = Offset(index * barSpacing, size.height - barHeight),
                size = size.copy(width = barWidth, height = barHeight)
            )
        }
    }
}

suspend fun bubbleSort(array: MutableList<Int>, comparedIndices: MutableState<Pair<Int, Int>>, delayTime: Long, onSortComplete: () -> Unit) {
    val size = array.size
    for (i in 0 until size - 1) {
        for (j in 0 until size - i - 1) {
            comparedIndices.value = Pair(j, j + 1)
            if (array[j] > array[j + 1]) {
                val temp = array[j]
                array[j] = array[j + 1]
                array[j + 1] = temp
            }
            delay(delayTime)
        }
    }
    comparedIndices.value = Pair(-1, -1)
    onSortComplete()
}

suspend fun mergeSort(array: MutableList<Int>, comparedIndices: MutableState<Pair<Int, Int>>, delayTime: Long, onSortComplete: () -> Unit) {
    if (array.size <= 1) return
    val middle = array.size / 2
    val left = array.subList(0, middle).toMutableList()
    val right = array.subList(middle, array.size).toMutableList()
    mergeSort(left, comparedIndices, delayTime) {}
    mergeSort(right, comparedIndices, delayTime) {}
    merge(array, left, right, comparedIndices, delayTime)
    delay(delayTime)
    onSortComplete()
}

suspend fun merge(result: MutableList<Int>, left: List<Int>, right: List<Int>, comparedIndices: MutableState<Pair<Int, Int>>, delayTime: Long) {
    var i = 0
    var j = 0
    var k = 0
    while (i < left.size && j < right.size) {
        comparedIndices.value = Pair(i, j)
        if (left[i] <= right[j]) {
            result[k] = left[i]
            i++
        } else {
            result[k] = right[j]
            j++
        }
        k++
        delay(delayTime)
    }
    while (i < left.size) {
        result[k] = left[i]
        i++
        k++
    }
    while (j < right.size) {
        result[k] = right[j]
        j++
        k++
    }
    comparedIndices.value = Pair(-1, -1)
}

suspend fun quickSort(array: MutableList<Int>, low: Int, high: Int, comparedIndices: MutableState<Pair<Int, Int>>, delayTime: Long, onSortComplete: () -> Unit) {
    if (low < high) {
        val pi = partition(array, low, high, comparedIndices, delayTime)
        quickSort(array, low, pi - 1, comparedIndices, delayTime) {}
        quickSort(array, pi + 1, high, comparedIndices, delayTime) {}
        delay(delayTime)
    }
    comparedIndices.value = Pair(-1, -1)
    onSortComplete()
}

suspend fun partition(array: MutableList<Int>, low: Int, high: Int, comparedIndices: MutableState<Pair<Int, Int>>, delayTime: Long): Int {
    val pivot = array[high]
    var i = low - 1
    for (j in low until high) {
        comparedIndices.value = Pair(j, high)
        if (array[j] < pivot) {
            i++
            val temp = array[i]
            array[i] = array[j]
            array[j] = temp
        }
        delay(delayTime)
    }
    val temp = array[i + 1]
    array[i + 1] = array[high]
    array[high] = temp
    return i + 1
}

suspend fun insertionSort(array: MutableList<Int>, comparedIndices: MutableState<Pair<Int, Int>>, delayTime: Long, onSortComplete: () -> Unit) {
    for (i in 1 until array.size) {
        val key = array[i]
        var j = i - 1
        while (j >= 0 && array[j] > key) {
            comparedIndices.value = Pair(j, j + 1)
            array[j + 1] = array[j]
            j--
            delay(delayTime)
        }
        array[j + 1] = key
    }
    comparedIndices.value = Pair(-1, -1)
    onSortComplete()
}

suspend fun selectionSort(array: MutableList<Int>, comparedIndices: MutableState<Pair<Int, Int>>, delayTime: Long, onSortComplete: () -> Unit) {
    for (i in 0 until array.size - 1) {
        var minIdx = i
        for (j in i + 1 until array.size) {
            comparedIndices.value = Pair(minIdx, j)
            if (array[j] < array[minIdx]) {
                minIdx = j
            }
            delay(delayTime)
        }
        val temp = array[minIdx]
        array[minIdx] = array[i]
        array[i] = temp
    }
    comparedIndices.value = Pair(-1, -1)
    onSortComplete()
}
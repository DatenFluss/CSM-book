package com.android.csm_book.presentation.content.graphs.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.PriorityQueue

class GraphAsGridViewModel : ViewModel() {
    private val _grid = MutableStateFlow(Grid(20, 20))
    val grid: StateFlow<Grid> = _grid

    private val _startNode = MutableStateFlow<Node?>(null)
    val startNode: StateFlow<Node?> = _startNode

    private val _endNode = MutableStateFlow<Node?>(null)
    val endNode: StateFlow<Node?> = _endNode

    private val _walls = MutableStateFlow<Set<Node>>(emptySet())
    val walls: StateFlow<Set<Node>> = _walls

    private val _searchPath = MutableStateFlow<List<Node>>(emptyList())
    val searchPath: StateFlow<List<Node>> = _searchPath

    private val _visitedNodes = MutableStateFlow<List<Node>>(emptyList())
    val visitedNodes: StateFlow<List<Node>> = _visitedNodes

    fun setStartNode(node: Node) {
        _startNode.value?.isStart = false
        _startNode.value = node
        node.isStart = true
        _grid.value = _grid.value // Trigger state update
    }

    fun setEndNode(node: Node) {
        _endNode.value?.isEnd = false
        _endNode.value = node
        node.isEnd = true
        _grid.value = _grid.value // Trigger state update
    }

    fun toggleWall(node: Node) {
        node.isWall = !node.isWall
        if (node.isWall) {
            _walls.value += node
        } else {
            _walls.value -= node
        }
        _grid.value = _grid.value // Trigger state update
    }

    fun resetGrid() {
        _grid.value.reset()
        _startNode.value = null
        _endNode.value = null
        _walls.value = emptySet()
        _searchPath.value = emptyList()
        _visitedNodes.value = emptyList()
        _grid.value = Grid(20, 20) // Trigger state update by re-assigning the grid
    }

    fun runDijkstra() {
        viewModelScope.launch {
            _grid.value.reset()
            _visitedNodes.value = emptyList()
            _searchPath.value = emptyList()

            val start = _startNode.value ?: return@launch
            val end = _endNode.value ?: return@launch

            val pq = PriorityQueue<Node>(compareBy { it.distance })
            start.distance = 0
            pq.add(start)

            val visited = mutableSetOf<Node>()
            val path = mutableListOf<Node>()

            while (pq.isNotEmpty()) {
                val current = pq.poll()
                if (current!! in visited) continue
                visited.add(current)
                _visitedNodes.value = visited.toList()
                delay(50) // Add delay for visualization

                if (current == end) break

                for (neighbor in _grid.value.getNeighbors(current)) {
                    if (neighbor.isWall || neighbor in visited) continue
                    val newDist = current.distance + 1
                    if (newDist < neighbor.distance) {
                        neighbor.distance = newDist
                        neighbor.previous = current
                        pq.add(neighbor)
                    }
                }
            }

            var current: Node? = end
            while (current != null) {
                path.add(current)
                _searchPath.value = path.reversed()
                current = current.previous
                delay(50)
            }
        }
    }

    fun runAStar() {
        viewModelScope.launch {
            _grid.value.reset()
            _visitedNodes.value = emptyList()
            _searchPath.value = emptyList()

            val start = _startNode.value ?: return@launch
            val end = _endNode.value ?: return@launch

            val openSet = PriorityQueue<Node>(compareBy { it.distance + it.heuristic })
            start.distance = 0
            start.heuristic = heuristic(start, end)
            openSet.add(start)

            val visited = mutableSetOf<Node>()
            val path = mutableListOf<Node>()

            while (openSet.isNotEmpty()) {
                val current = openSet.poll()
                if (current!! in visited) continue
                visited.add(current)
                _visitedNodes.value = visited.toList()
                delay(50)

                if (current == end) break

                for (neighbor in _grid.value.getNeighbors(current)) {
                    if (neighbor.isWall || neighbor in visited) continue
                    val newDist = current.distance + 1
                    if (newDist < neighbor.distance) {
                        neighbor.distance = newDist
                        neighbor.heuristic = heuristic(neighbor, end)
                        neighbor.previous = current
                        openSet.add(neighbor)
                    }
                }
            }

            var current: Node? = end
            while (current != null) {
                path.add(current)
                _searchPath.value = path.reversed()
                current = current.previous
                delay(50)
            }
        }
    }

    private fun heuristic(node: Node, end: Node): Int {
        return kotlin.math.abs(node.x - end.x) + kotlin.math.abs(node.y - end.y)
    }
}

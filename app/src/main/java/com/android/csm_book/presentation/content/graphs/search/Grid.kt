package com.android.csm_book.presentation.content.graphs.search

data class Node(
    val x: Int,
    val y: Int,
    var isStart: Boolean = false,
    var isEnd: Boolean = false,
    var isWall: Boolean = false,
    var distance: Int = Int.MAX_VALUE,
    var heuristic: Int = 0,
    var previous: Node? = null
)

class Grid(val width: Int, val height: Int) {
    val nodes: Array<Array<Node>> = Array(width) { x ->
        Array(height) { y ->
            Node(x, y)
        }
    }

    fun getNeighbors(node: Node): List<Node> {
        val neighbors = mutableListOf<Node>()
        val directions = listOf(
            Pair(-1, 0), Pair(1, 0), Pair(0, -1), Pair(0, 1)
        )
        for ((dx, dy) in directions) {
            val newX = node.x + dx
            val newY = node.y + dy
            if (newX in 0 until width && newY in 0 until height) {
                neighbors.add(nodes[newX][newY])
            }
        }
        return neighbors
    }

    fun reset() {
        for (row in nodes) {
            for (node in row) {
                node.distance = Int.MAX_VALUE
                node.heuristic = 0
                node.previous = null
            }
        }
    }
}

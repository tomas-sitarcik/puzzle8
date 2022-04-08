import Move.*


fun main(args: Array<String>) {

    val initialState =
        arrayOf(
            intArrayOf(0, 1, 3),
            intArrayOf(4, 2, 5),
            intArrayOf(7, 8, 6)
        )

    val desiredState =
        arrayOf(
            intArrayOf(1, 2, 3),
            intArrayOf(4, 5, 6),
            intArrayOf(7, 8, 0)
        )

    val firstNode = Node(null, initialState)
    firstNode.generation = 1

    var lastGeneration: ArrayList<Node> = arrayListOf()
    makeChildren(firstNode)
    lastGeneration.addAll(firstNode.children)

    val moveList = arrayListOf<Move>()

    lastGeneration.add(firstNode)

    for (i in 0..1500) {
        val newGeneration: ArrayList<Node> = arrayListOf()
        for (node in lastGeneration) {
            makeChildren(node)
            newGeneration.addAll(node.children)
        }

        var result = newGeneration.find { it.state.contentDeepEquals(desiredState) }

        if (result != null) {
            result.diff?.let { moveList.add(it) }
            result = result.parent
            if (result != null) {
                while (result?.parent != null) {
                    //println(result.diff)
                    result.diff?.let { moveList.add(it) }
                    result = result.parent
                }
            }
            moveList.reverse()
            println(moveList)
            break
        }

        val uniqueStates: ArrayList<Array<IntArray>> = arrayListOf()
        lastGeneration = arrayListOf()
        for (node in newGeneration) {

            if (uniqueStates.isEmpty()) {
                uniqueStates.add(node.state)
                lastGeneration.add(node)
            }

            if (uniqueStates.find { it.contentDeepEquals(node.state) } == null) {
                uniqueStates.add(node.state)
                lastGeneration.add(node)
            }


        }

        println("this generation had ${lastGeneration.size} unique nodes")
    }
}

fun Array<IntArray>.toString(): String {
    var string = ""
    for (row in this) {
        var string = ""
        for (number in row) {
            string += number
        }
        string+="\n"
    }
    return string
}

fun Array<IntArray>.copy() = Array(size) { get(it).clone() }

fun getZeroIndex(state: Array<IntArray>): Pair<Int, Int> {
    for (i in 0..2) {
        for (y in 0..2) {
            if (state[i][y] == 0) {
                return Pair(i, y)
            }
        }
    }
    return Pair(-1, -1)
}

fun move(index: Pair<Int, Int>, direction: Move, _state: Array<IntArray>): Array<IntArray> {

    val state = _state.copy()

    fun swap(indexA: Pair<Int, Int>, indexB: Pair<Int, Int>, state: Array<IntArray>) {
        val temp = state[indexA.first][indexA.second]
        state[indexA.first][indexA.second] = state[indexB.first][indexB.second]
        state[indexB.first][indexB.second] = temp
    }

    try {
        when (direction) {
            N -> {
                swap(index, Pair(index.first - 1, index.second), state)
            }
            E -> {
                swap(index, Pair(index.first, index.second + 1), state)
            }
            S -> {
                swap(index, Pair(index.first + 1, index.second), state)
            }
            W -> {
                swap(index, Pair(index.first, index.second - 1), state)
            }
        }
    } catch (e: ArrayIndexOutOfBoundsException) {
        return arrayOf()
    }

    return state

}

fun makeChildren(node: Node) {
    val zeroIndex = getZeroIndex(node.state)
    if (zeroIndex.first == -1) {
        return
    }

    for (move in Move.values()) {
        val tempState = move(zeroIndex, move, node.state)
        if (tempState.isNotEmpty()) {
            node.children.add(Node(node, tempState, move))
        }
    }
}

enum class Move {
    N, E, S, W
}

class Node(_parent: Node?, _state: Array<IntArray> = arrayOf(), _diff: Move? = null) {
    val state = _state
    val parent: Node? = _parent
    var children: ArrayList<Node> = arrayListOf()
    val diff = _diff // what move was made on parent to make the child
    var generation: Int = parent?.generation?.plus(1) ?: 1

    override fun toString(): String {
        var string = "Node\n"
        for (row in state) {
            for (element in row) {
                string += element
            }
            string += "\n"
        }
        return string
    }
}
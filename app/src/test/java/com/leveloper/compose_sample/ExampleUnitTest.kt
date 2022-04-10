package com.leveloper.compose_sample

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test

class AmazingCircleTest {

    class AmazingCircle(private val n: Int) {

        private val visit = Array(n + 1) { false }
        private val path = Array(n + 1) { 0 }

        // 1, 4, 9, 16, 25, 36, 49, ...
        private val squareSet: Set<Int> = mutableSetOf<Int>().apply {
            var root = 1
            while (root * root < 2 * n) {
                add(root * root)
                root++
            }
        }

        private val graph: Array<Set<Int>> = Array(n + 1) { i ->
            if (i == 0) return@Array emptySet()

            squareSet.asSequence()
                .filter { square -> square > i && square - i <= n }
                .mapNotNull { square -> (square - i).takeIf { it != i } }
                .toSet()
        }

        fun valid(): Boolean = hamiltonian(n, 1)

        private fun hamiltonian(target: Int, index: Int): Boolean {
            if (index == n + 1) {
                val start = path[1]
                val end = path[n]

                // 시작점과 끝나는 지점이 연결되는지 체크
                if (graph[start].contains(end)) return true
            }

            for (i in graph[target]) {
                if (visit[i]) continue

                visit[i] = true
                path[index] = i

                val isFinish = hamiltonian(i, index + 1)
                if (isFinish) return true

                visit[i] = false
                path[index] = 0
            }

            return false
        }
    }

    @Test
    fun `1부터 n까지의 수가 있을 때 amazing circle이 되는지에 대해 판별하라`() = runBlocking {
        val n = 32
        val amazingCircle = AmazingCircle(n)

        assertTrue(amazingCircle.valid())
    }
}
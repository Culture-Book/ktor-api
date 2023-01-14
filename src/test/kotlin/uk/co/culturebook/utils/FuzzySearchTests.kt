package uk.co.culturebook.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class FuzzySearchTests {
    @Test
    fun `toVector should return correct character frequency map`() {
        val input = "hello world"
        val expected = mapOf('h' to 1, 'e' to 1, 'l' to 3, 'o' to 2, 'w' to 1, 'r' to 1, 'd' to 1)
        val result = input.toVector()
        assertEquals(expected, result)
    }

    @Test
    fun `getCosine should return correct similarity score`() {
        val vectorA = mapOf('a' to 2, 'b' to 1, 'c' to 1)
        val vectorB = mapOf('a' to 1, 'b' to 2, 'c' to 1)
        val expected = 0.8
        val result = getCosine(vectorA, vectorB)
        assertEquals(expected, result, 0.1)
    }

    @Test
    fun `matchStrings should return correct similarity score`() {
        val a = "hello world"
        val b = "hello"
        val expected = 0.8
        val result = matchStrings(a, b)
        assertEquals(expected, result, 0.15)
    }

    @Test
    fun `fuzzySearchStrings should return correct list of matches`() {
        val input = listOf("hello", "world", "hell", "worl", "helo")
        val search = "hello"
        val expected = listOf(Pair("hello", 1.0), Pair("helo", 0.9), Pair("hell", 0.8))
        val results = input.fuzzySearchStrings(search)
        expected.forEachIndexed { i, pair ->
            assertEquals(results[i].first, pair.first)
            assertEquals(results[i].second, pair.second, 0.15)
        }
    }
}

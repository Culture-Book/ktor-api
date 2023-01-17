package uk.co.culturebook.utils

import kotlin.math.pow
import kotlin.math.sqrt

fun String.toVector(): Map<Char, Int> {
    val string = replace(" ", "")
    val vector: MutableMap<Char, Int> = emptyMap<Char, Int>().toMutableMap()
    string.forEach { character ->
        vector[character] = (vector[character] ?: 0) + 1
    }
    return vector
}

fun getCosine(vectorA: Map<Char, Int>, vectorB: Map<Char, Int>): Double {
    val intersection = vectorA.keys.toSet().intersect(vectorB.keys)
    val numerator = intersection.fold(0) { prev, char -> prev + (vectorA[char]?.times(vectorB[char] ?: 1) ?: 0) }

    val sumA = vectorA.keys.fold(0.0) { prev, char ->
        prev + vectorA[char]!!.toDouble().pow(2)
    }
    val sumB = vectorB.keys.fold(0.0) { prev, char ->
        prev + vectorB[char]!!.toDouble().pow(2)
    }

    val denominator = sqrt(sumA) * sqrt(sumB)
    return numerator / (denominator + Double.MIN_VALUE)
}

fun matchStrings(a: String, b: String): Double = getCosine(a.toVector(), b.toVector())

fun List<String>.fuzzySearchStrings(search: String, weight: Double = 0.0): List<Pair<String, Double>> {
    val fuzzyMap = emptyMap<String, Double>().toMutableMap()
    forEach { string ->
        val cosine = matchStrings(search, string)
        if (cosine >= weight) fuzzyMap[string] = cosine
    }
    return fuzzyMap.toList().sortedByDescending { (_, value) -> value }
}
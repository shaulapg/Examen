package com.app.examen.domain.model

data class Sudoku (
    val puzzle: List<List<Int?>> = emptyList(),
    val solution: List<List<Int>>?,
    val guesses: List<MutableList<Int>>? = emptyList(),
    val width: Int? = null,
    val height: Int? = null,
    val difficulty: String? = null,
    val solved: String? = null,
    val status: String? = null,
)
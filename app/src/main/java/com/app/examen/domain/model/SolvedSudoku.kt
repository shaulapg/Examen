package com.app.examen.domain.model

data class SolvedSudoku (
    val puzzle: String? = null,
    val solution: List<List<Int>>? = null,
    val status: String? = null,
    val width: Int? = null,
    val height: Int? = null
)
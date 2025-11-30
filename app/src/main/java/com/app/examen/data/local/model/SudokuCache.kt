package com.app.examen.data.local.model

data class SudokuCache (
    val puzzle: List<List<Int?>>,
    val guess: List<MutableList<Int>>?,
    val solution: List<List<Int>>,
    val width: Int,
    val height: Int,
    val difficulty: String,
    val savedAt: Long,
    val lastUpdate: Long = 0,
)
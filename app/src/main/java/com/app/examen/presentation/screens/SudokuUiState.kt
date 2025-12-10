package com.app.examen.presentation.screens

data class SudokuUiState (
    val puzzle: List<List<Int?>> = emptyList(),
    val solution: List<List<Int>>? = emptyList(),
    val width: Int? = 3,
    val height: Int? = 3,
    val guesses: List<MutableList<Int>>? = List((width?:3) * (width?:3)) { MutableList((height?:3) * (height?:3)) { 0 } },
    val difficulty: String = "",
    var solved: Boolean = false,
    var ready: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccessful: Boolean = false,
    val verificationMessage: String? = null,
)
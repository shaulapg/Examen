package com.app.examen.domain.repository

import com.app.examen.domain.model.Sudoku

interface SudokuRepository {
    suspend fun getSudoku(width: Int?, height: Int?, difficulty: String?, newSudoku: Boolean): Sudoku
}
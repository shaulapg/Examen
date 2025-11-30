package com.app.examen.data.mapper

import com.app.examen.data.remote.dto.SudokuDto
import com.app.examen.domain.model.Sudoku

fun SudokuDto.toDomain(): Sudoku =
    Sudoku(
        puzzle = puzzle,
        solution = solution,
        guesses = emptyList()
    )
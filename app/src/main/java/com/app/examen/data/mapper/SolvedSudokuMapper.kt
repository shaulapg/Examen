package com.app.examen.data.mapper

import com.app.examen.data.remote.dto.SolvedSudokuDto
import com.app.examen.domain.model.SolvedSudoku

fun SolvedSudokuDto.toDomain(): SolvedSudoku =
    SolvedSudoku(
        status = status,
        solution = solution,
    )
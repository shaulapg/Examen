package com.app.examen.domain.usecase

import com.app.examen.domain.common.Result
import com.app.examen.domain.model.Sudoku
import com.app.examen.domain.repository.SudokuRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetSudokuUseCase
    @Inject
    constructor(
        private val repository: SudokuRepository,
    ) {
        operator fun invoke(width: Int, height: Int, difficulty: String, newSudoku: Boolean): Flow<Result<Sudoku>> =
            flow {
                try {
                    emit(Result.Loading)
                    val sudoku = repository.getSudoku(width, height, difficulty, newSudoku)
                    emit(Result.Success(sudoku))
                } catch (e: Exception) {
                    emit(Result.Error(e))
                }
            }
    }
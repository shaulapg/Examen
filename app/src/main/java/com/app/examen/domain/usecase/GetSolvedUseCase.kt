package com.app.examen.domain.usecase

import com.app.examen.domain.common.Result
import com.app.examen.domain.model.SolvedSudoku
import com.app.examen.domain.model.Sudoku
import com.app.examen.domain.repository.SudokuRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetSolvedUseCase
@Inject
constructor(
    private val repository: SudokuRepository,
) {
    operator fun invoke(width: Int, height: Int, puzzle: String): Flow<com.app.examen.domain.common.Result<SolvedSudoku>> =
        flow {
            try {
                emit(com.app.examen.domain.common.Result.Loading)
                val sudoku = repository.getSolved(width, height, puzzle)
                emit(com.app.examen.domain.common.Result.Success(sudoku))
            } catch (e: Exception) {
                emit(Result.Error(e))
            }
        }
}
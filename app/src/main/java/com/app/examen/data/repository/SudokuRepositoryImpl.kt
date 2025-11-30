package com.app.examen.data.repository

import com.app.examen.data.local.preferences.SudokuPreferences
import com.app.examen.data.mapper.toDomain
import com.app.examen.data.remote.api.SudokuApi
import com.app.examen.domain.model.Sudoku
import com.app.examen.domain.repository.SudokuRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SudokuRepositoryImpl
    @Inject
    constructor(
        private val api: SudokuApi,
        private val preferences: SudokuPreferences,
    ) : SudokuRepository {
    override suspend fun getSudoku(width: Int?, height: Int?, difficulty: String?, newSudoku: Boolean): Sudoku {
        preferences.getSudokuCache()?.let { cache ->
            if (preferences.isCacheValid() && !newSudoku) {
                return Sudoku(
                    puzzle = cache.puzzle,
                    solution = cache.solution,
                    guesses = cache.guess,
                    width = cache.width,
                    height = cache.height,
                    difficulty = cache.difficulty
                )
            }
        }
        return try {
            val response = api.getSudoku(
                width = width ?: 3,
                height = height ?: 3,
                difficulty = difficulty ?: "Medium"
            ).toDomain()
            val data = Sudoku(
                puzzle = response.puzzle,
                solution = response.solution,
            )

            val newSize = (width ?: 3) * (height ?: 3)
            val newGuess = List(newSize) { MutableList(newSize) { 0 } }

            data.puzzle.mapIndexed { r, rowList ->
                rowList.mapIndexed { c, value ->
                    if (value != null) {
                        newGuess[r][c] = value
                    }
                }
            }
            preferences.saveSudoku(
                puzzle = data.puzzle,
                solution = data.solution?: emptyList(),
                guess = newGuess,
                width = width?: 0,
                height = height?: 0,
                difficulty = difficulty?: ""
            )
            data
        } catch (e: Exception) {
            preferences.getSudokuCache()?.let { cache ->
                Sudoku(
                    puzzle = cache.puzzle,
                    solution = cache.solution,
                    width = cache.width,
                    height = cache.height,
                    difficulty = cache.difficulty
                )
            } ?: throw e
        }
    }
}
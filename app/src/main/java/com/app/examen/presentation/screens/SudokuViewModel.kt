package com.app.examen.presentation.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.examen.data.local.preferences.SudokuPreferences
import com.app.examen.domain.repository.SudokuRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class SudokuViewModel @Inject constructor(
    private val repository: SudokuRepository,
    private val preferences: SudokuPreferences
) : ViewModel() {
    private val _uiState = MutableStateFlow(SudokuUiState())
    val uiState: StateFlow<SudokuUiState> = _uiState.asStateFlow()

    fun loadSudoku(difficulty: String? = null, width: Int? = null, height: Int? = null, loadSaved: Boolean = false) {
        viewModelScope.launch {
            _uiState.value = SudokuUiState(
                error = null,
                isLoading = true
            )
            try {
                if (loadSaved) {
                    val saved = preferences.getSudokuCache()
                    if (saved != null) {
                        _uiState.value = SudokuUiState(
                            guesses = saved.guess as List<MutableList<Int>>,
                            solution = saved.solution,
                            puzzle = saved.puzzle,
                            width = saved.width,
                            height = saved.height,
                            difficulty = saved.difficulty,
                            isLoading = false,
                            isSuccessful = true
                        )
                        preferences.clearCache()
                        return@launch
                    }
                } else {
                    try {
                        val sudoku = repository.getSudoku(width, height, difficulty, true)
                        _uiState.value = SudokuUiState(
                            puzzle = sudoku.puzzle,
                            solution = sudoku.solution,
                            guesses = sudoku.guesses,
                            width = width ?: 3,
                            height = height ?: 3,
                            difficulty = difficulty ?: "medium",
                            isLoading = false,
                        )
                    } catch (e: IOException) {
                        _uiState.value = SudokuUiState(
                            isLoading = false,
                            error = if (e.message?.contains("Unable to resolve host") == true) {
                                "Failed to connect to the server. Check your internet connection."
                            } else {
                                e.message
                            }
                        )
                    } catch (e: Exception) {
                        _uiState.value = SudokuUiState(
                            isLoading = false,
                            error = e.message ?: "Unknown error"
                        )
                    }
                }

            } catch (e: IOException) {
                _uiState.value = SudokuUiState(
                    isLoading = false,
                    error = if (e.message?.contains("Unable to resolve host") == true) {
                        "Failed to connect to the server. Check your internet connection."
                    } else {
                        e.message
                    }
                )
            } catch (e: Exception) {
                _uiState.value = SudokuUiState(
                    isLoading = false,
                    error = e.message ?: "Unknown error"
                )
            }
        }
    }

    fun saveSudoku() {
        val ui = _uiState.value
        if (ui.isSuccessful) {
            preferences.saveSudoku(
                puzzle = ui.puzzle,
                solution = ui.solution as List<List<Int>>,
                width = ui.width?: 3,
                height = ui.height?: 3,
                guess = ui.guesses as List<MutableList<Int>>,
                difficulty = ui.difficulty
            )
        }
    }

    fun setInitialGuesses(puzzle: List<List<Int?>>) {
        val newGuesses = puzzle.map { row ->
            row.map { it ?: 0 }.toMutableList()
        }

        _uiState.value = _uiState.value.copy(guesses = newGuesses)
    }

    fun updateGuesses(row: Int, col: Int, value: Int) {
        _uiState.value.guesses?.get(row)?.set(col, value)
    }

    fun checkSudoku(puzzle: String, width: Int, height: Int) {
        viewModelScope.launch {
            val solved = try {
                val apiResponse = repository.getSolved(width, height, puzzle)
                apiResponse.solution ?: _uiState.value.solution
            } catch (e: Exception) {
                _uiState.value.solution
            }
            _uiState.update { currentState ->
                currentState.copy(
                    solved = solved == _uiState.value.guesses,
                    ready = true
                )
            }
        }
    }

    fun endGame(){
        preferences.clearCache()
    }
}
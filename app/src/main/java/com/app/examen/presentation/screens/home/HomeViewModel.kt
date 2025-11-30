package com.app.examen.presentation.screens.home

import androidx.lifecycle.ViewModel
import com.app.examen.data.local.preferences.SudokuPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val preferences: SudokuPreferences
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    fun isGameSaved() {
        _uiState.value = _uiState.value.copy(
            saved = preferences.getSudokuCache() != null
        )
    }
}
package com.app.examen.data.local.preferences

import android.content.Context
import android.content.SharedPreferences
import com.app.examen.data.local.model.SudokuCache
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SudokuPreferences
    @Inject
    constructor(
        @ApplicationContext context: Context,
        private val gson: Gson,
    ) {
        private val prefs: SharedPreferences =
            context.getSharedPreferences(
                PreferencesConstants.PREF_NAME,
                Context.MODE_PRIVATE,
            )

        fun saveSudoku(
            puzzle: List<List<Int?>>,
            guess: List<MutableList<Int>>?,
            solution: List<List<Int>>,
            width: Int,
            height: Int,
            difficulty: String,
        ){
            val game = SudokuCache(
                puzzle = puzzle,
                guess = guess,
                solution = solution,
                width = width,
                height = height,
                difficulty = difficulty,
                savedAt = System.currentTimeMillis()
            )

            println("Game: $game")

            prefs
                .edit()
                .putString(PreferencesConstants.KEY_SUDOKU_CACHE, gson.toJson(game))
                .putLong(PreferencesConstants.KEY_LAST_UPDATE, System.currentTimeMillis())
                .apply()
        }

        fun getSudokuCache(): SudokuCache? {
            val json = prefs.getString(PreferencesConstants.KEY_SUDOKU_CACHE, null)
            val lastUpdate = prefs.getLong(PreferencesConstants.KEY_LAST_UPDATE, 0)

            if (json == null) return null

            val type = object : TypeToken<SudokuCache>() {}.type
            val sudokuCache = gson.fromJson<SudokuCache>(json, type)

            return SudokuCache (
                puzzle = sudokuCache.puzzle,
                guess = sudokuCache.guess,
                solution = sudokuCache.solution,
                width = sudokuCache.width,
                height = sudokuCache.height,
                difficulty = sudokuCache.difficulty,
                savedAt = sudokuCache.savedAt,
                lastUpdate = lastUpdate
            )
        }

        fun clearCache() {
            prefs
                .edit()
                .clear()
                .apply()
        }

        fun isCacheValid(): Boolean {
            val lastUpdate = prefs.getLong(PreferencesConstants.KEY_LAST_UPDATE, 0)
            return System.currentTimeMillis() - lastUpdate < PreferencesConstants.CACHE_DURATION
        }
    }
package com.app.examen.data.remote.api

import com.app.examen.data.remote.dto.SolvedSudokuDto
import com.app.examen.data.remote.dto.SudokuDto
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface SudokuApi {
    @GET("sudokugenerate")
    suspend fun getSudoku(
        @Header("X-Api-Key") apiKey: String = "Q2xsyWglKPzSK4yp8vSQOg==PDm9MUSRmg1nWYnF",
        @Query("width") width: Int? = 3,
        @Query("height") height: Int? = 3,
        @Query("difficulty") difficulty: String? = "medium",
    ): SudokuDto

    @GET("sudokusolve")
    suspend fun getSolved(
        @Header("X-Api-Key") apiKey: String = "Q2xsyWglKPzSK4yp8vSQOg==PDm9MUSRmg1nWYnF",
        @Header("Content-Type") contentType: String = "application/json",
        @Query("puzzle") puzzle: String,
        @Query("width") width: Int? = 3,
        @Query("height") height: Int? = 3,
    ): SolvedSudokuDto
}
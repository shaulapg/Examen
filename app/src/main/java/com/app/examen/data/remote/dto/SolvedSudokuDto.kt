package com.app.examen.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SolvedSudokuDto (
    @SerializedName("status") val status: String? = null,
    @SerializedName("solution") val solution: List<List<Int>>? = null,
)
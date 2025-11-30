package com.app.examen.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SudokuParamsDto (
    @SerializedName("width") val width: Int? = null,
    @SerializedName("height") val height: Int? = null,
    @SerializedName("difficulty") val difficulty: String? = null,
)
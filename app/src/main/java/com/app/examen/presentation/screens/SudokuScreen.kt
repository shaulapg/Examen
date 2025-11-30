package com.app.examen.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

data class CellUiModel(
    val value: Int?,
    val isFixed: Boolean,
    val row: Int,
    val col: Int
)

@Composable
fun SudokuScreen(
    difficulty: String,
    width: Int,
    height: Int,
    viewModel: SudokuViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val puzzle = uiState.puzzle

    when {
        uiState.isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        uiState.error != null -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = uiState.error!!,
                    color = Color.Red,
                    modifier = Modifier.padding(30.dp),
                    textAlign = TextAlign.Center
                )
                Button(onClick = {
                    viewModel.loadSudoku(difficulty, width, height)
                }) {
                    Text("Reintentar")
                }
                Button(onClick = onBackClick) {
                    Text("Regresar")
                }
            }
        }

        else -> {
            println("Puzzle: $puzzle")
            println("Guess ${uiState.guesses}")
            if (difficulty != "Cont") {
                LaunchedEffect(difficulty, width, height) {
                    viewModel.loadSudoku(difficulty, width, height)
                }
                LaunchedEffect(puzzle) {
                    if (puzzle.isNotEmpty()) {
                        viewModel.setInitialGuesses(puzzle)
                    }
                }
            } else
                viewModel.loadSudoku(loadSaved = true)

            viewModel.saveSudoku()

            if (puzzle.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Error: No sudoku found",
                        color = Color.Red,
                        modifier = Modifier.padding(30.dp),
                        textAlign = TextAlign.Center
                    )
                    Button(onClick = {
                        viewModel.loadSudoku(difficulty, width, height)
                    }) {
                        Text("Reintentar")
                    }
                    Button(onClick = onBackClick) {
                        Text("Regresar")
                    }
                }
            } else {
                var boardState by remember(puzzle) {
                    mutableStateOf(
                        puzzle.mapIndexed { r, rowList ->
                            rowList.mapIndexed { c, value ->
                                CellUiModel(
                                    isFixed = puzzle[r][c] != null,
                                    value = value
                                        ?: if(!uiState.guesses.isNullOrEmpty())
                                            if (uiState.guesses?.get(r)[c] != 0)
                                                uiState.guesses?.get(r)[c]
                                            else null
                                        else null,
                                    row = r,
                                    col = c
                                )
                            }
                        }
                    )
                }

                var selectedCell by remember { mutableStateOf<Pair<Int, Int>?>(null) }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Button(
                        onClick = onBackClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA8A8A8)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .size(100.dp, 30.dp)
                            .align(AbsoluteAlignment.Left),
                        contentPadding = PaddingValues(0.dp),
                    ) {
                        Text(text = "New Sudoku", fontSize = 15.sp, fontWeight = FontWeight.Normal, color = Color.Black)
                    }

                    Text(
                        "Sudoku ($difficulty)",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 20.dp)
                    )

                    DynamicSudokuBoard(
                        board = boardState,
                        width = width,
                        height = height,
                        selectedCell = selectedCell,
                        onCellClick = { r, c -> selectedCell = r to c }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    SudokuNumberPad(
                        onNumberClick = { number ->
                            selectedCell?.let { (r, c) ->
                                if (!boardState[r][c].isFixed) {
                                    val newBoard =
                                        boardState.map { row -> row.toMutableList() }
                                            .toMutableList()

                                    newBoard[r][c] = newBoard[r][c].copy(value = number)
                                    viewModel.updateGuesses(r, c, number)

                                    boardState = newBoard
                                }
                            }
                        },
                        onDeleteClick = {
                            selectedCell?.let { (r, c) ->
                                if (!boardState[r][c].isFixed) {
                                    val newBoard =
                                        boardState.map { row -> row.toMutableList() }
                                            .toMutableList()
                                    newBoard[r][c] = newBoard[r][c].copy(value = null)
                                    viewModel.updateGuesses(r, c, 0)
                                    boardState = newBoard
                                }
                            }
                        },
                        onResetClick = {
                            val resetBoard = boardState.map { row ->
                                row.map { cell ->
                                    if (cell.isFixed) {
                                        cell
                                    } else {
                                        viewModel.updateGuesses(cell.row, cell.col, 0)
                                        cell.copy(value = null)
                                    }
                                }
                            }
                            boardState = resetBoard
                        },
                        onCheckClick = {
                            //Didn't work
                        },
                        numberpad = width * height
                    )
                }
            }
        }
    }
}

@Composable
fun DynamicSudokuBoard(
    board: List<List<CellUiModel>>,
    width: Int,
    height: Int,
    selectedCell: Pair<Int, Int>?,
    onCellClick: (Int, Int) -> Unit
) {
    val rows = board.size
    val cols = board.first().size

    Box(
        modifier = Modifier
            .border(2.dp, Color.Black)
            .background(Color.White)
    ) {
        Column {
            for (r in 0 until rows) {
                Row {
                    for (c in 0 until cols) {
                        val isThickRight = (c + 1) % width == 0 && c != cols - 1
                        val isThickBottom = (r + 1) % height == 0 && r != rows - 1
                        val isSelected = selectedCell?.let { it.first == r && it.second == c } ?: false

                        Box(
                            modifier = Modifier
                                .drawGridBorders(
                                    thickRight = isThickRight,
                                    thickBottom = isThickBottom
                                )
                        ) {
                            SudokuCell(
                                cell = board[r][c],
                                isSelected = isSelected,
                                onClick = { onCellClick(r, c) },
                                width = width
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SudokuCell(
    cell: CellUiModel,
    isSelected: Boolean,
    onClick: () -> Unit,
    width: Int,
) {
    val bgColor = when {
        isSelected -> Color(0xFFBBDEFB)
        cell.isFixed -> Color(0xFFEEEEEE)
        else -> Color.White
    }

    val textColor = if (cell.isFixed) Color.Black else Color(0xFF1565C0)
    val fontWeight = if (cell.isFixed) FontWeight.Bold else FontWeight.Normal
    val cellValue = 360 / (width * width)
    val fontValue = 160 / (width * width)

    Box(
        modifier = Modifier
            .size(cellValue.dp)
            .background(bgColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (cell.value != null) {
            Text(
                text = cell.value.toString(),
                fontSize = fontValue.sp,
                fontWeight = fontWeight,
                color = textColor
            )
        }
    }
}

fun Modifier.drawGridBorders(
    thickRight: Boolean,
    thickBottom: Boolean
) = this.drawWithContent {
    drawContent()

    val thickStroke = 2.dp.toPx()
    val thinStroke = 1.dp.toPx()
    val color = Color.Black
    val strokeWidthRight = if (thickRight) thickStroke else thinStroke
    val strokeWidthBottom = if (thickBottom) thickStroke else thinStroke

    drawLine(
        color = color,
        start = Offset(size.width, 0f),
        end = Offset(size.width, size.height),
        strokeWidth = strokeWidthRight
    )

    drawLine(
        color = color,
        start = Offset(0f, size.height),
        end = Offset(size.width, size.height),
        strokeWidth = strokeWidthBottom
    )
}

@Composable
fun SudokuNumberPad(
    modifier: Modifier = Modifier,
    onNumberClick: (Int) -> Unit,
    onDeleteClick: () -> Unit,
    onResetClick: () -> Unit,
    onCheckClick: () -> Unit,
    numberpad: Int,
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val divisible = if (numberpad % 4 == 0) {
            4
        } else {
            3
        }

        for (i in 1..(numberpad / divisible)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ((divisible * (i - 1) + 1)..(divisible * i)).forEach { num ->
                    NumberButton(number = num, onClick = { onNumberClick(num) })
                }
            }
        }

        Row {
            Button(
                onClick = onDeleteClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF5350)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(50.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Number",
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = onResetClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA8A8A8)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(100.dp, 50.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(text = "Reset", fontSize = 20.sp, fontWeight = FontWeight.Normal, color = Color.Black)
            }

            Button(
                onClick = onCheckClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF76A67B)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(100.dp, 50.dp),
                contentPadding = PaddingValues(0.dp),
            ) {
                Text(text = "Results", fontSize = 20.sp, fontWeight = FontWeight.Normal, color = Color.Black)
            }
        }
    }
}

@Composable
fun NumberButton(number: Int, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.size(50.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(text = number.toString(), fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun LockedPopup(
    message: String,
    secondMessage: String,
    onAccept: () -> Unit
) {
    Dialog (
        onDismissRequest = {}
    ) {
        Box(
            modifier = Modifier
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(onClick = onAccept) {
                    Text(secondMessage)
                }
            }
        }
    }
}
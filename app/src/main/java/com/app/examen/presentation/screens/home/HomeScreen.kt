package com.app.examen.presentation.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onStart: (difficulty: String, width: Int, height: Int) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState.collectAsState().value
    val difficulties = listOf("easy", "medium", "hard")
    val sizes = listOf("2 x 2", "3 x 3", "4 x 4")
    var difficulty by remember { mutableStateOf(difficulties.first()) }
    var width by remember { mutableIntStateOf(3) }
    var height by remember { mutableIntStateOf(3) }
    var diffExpanded by remember { mutableStateOf(false) }
    var sizeExpanded by remember { mutableStateOf(false) }

    viewModel.isGameSaved()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Select Sudoku", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(32.dp))

        ExposedDropdownMenuBox(
            expanded = diffExpanded,
            onExpandedChange = { diffExpanded = !diffExpanded },
        ) {
            OutlinedTextField(
                value = difficulty,
                onValueChange = {},
                readOnly = true,
                label = { Text("Difficulty") },
                modifier = Modifier.menuAnchor(),
            )
            ExposedDropdownMenu(
                expanded = diffExpanded,
                onDismissRequest = { diffExpanded = false },
            ) {
                difficulties.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item) },
                        onClick = {
                            difficulty = item
                            diffExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        ExposedDropdownMenuBox(
            expanded = sizeExpanded,
            onExpandedChange = { sizeExpanded = !sizeExpanded },
        ) {
            OutlinedTextField(
                value = "${height} x ${width}",
                onValueChange = {},
                readOnly = true,
                label = { Text("Size (2-4)") },
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = sizeExpanded,
                onDismissRequest = { sizeExpanded = false }
            ) {
                sizes.forEachIndexed { index, s ->
                    DropdownMenuItem(
                        text = { Text(s) },
                        onClick = {
                            height = index + 2
                            width = index + 2
                            sizeExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = { onStart(difficulty, width, height) },
            modifier = Modifier.width(200.dp)
        ) {
            Text("Generate Sudoku")
        }


        Spacer(Modifier.height(64.dp))

        if (uiState.saved) {
            Button(
                onClick = { onStart("Cont", 3, 3) },
                modifier = Modifier.width(200.dp)
            ) {
                Text("Continue Sudoku")
            }
        }
    }
}

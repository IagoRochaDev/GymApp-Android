package com.example.gymapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.gymapp.ui.viewmodel.DetalhesTreinoViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import com.example.gymapp.data.local.Exercicio

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalhesTreinoScreen(
    treinoId: Int,
    onBack: () -> Unit,
    viewModel: DetalhesTreinoViewModel = hiltViewModel()
) {
    LaunchedEffect(treinoId) {
        viewModel.carregarTreino(treinoId)
    }

    val treinoComExercicios by viewModel.treinoSelecionado.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var exercicioParaEditar by remember { mutableStateOf<Exercicio?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(treinoComExercicios?.treino?.nome ?: "Carregando...") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { 
                exercicioParaEditar = null
                showDialog = true 
            }) {
                Icon(Icons.Default.Add, contentDescription = "Novo Exercício")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Se a lista estiver vazia ou nula
            if (treinoComExercicios?.exercicios.isNullOrEmpty()) {
                Text(
                    "Nenhum exercício ainda.",
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                LazyColumn {
                    items(
                        items = treinoComExercicios!!.exercicios,
                        key = { it.id }
                    ) { exercicio ->
                        ExercicioCard(
                            exercicio = exercicio,
                            onLongClick = { 
                                exercicioParaEditar = exercicio
                                showDialog = true
                            },
                            onDismiss = { viewModel.deletarExercicio(exercicio) },
                            onCheckClick = { novoSatus ->
                                viewModel.toggleConcluido(exercicio)
                            }
                        )
                    }
                }
            }
        }

        if (showDialog) {
            AdicionarExercicioDialog(
                exercicioParaEditar = exercicioParaEditar,
                onDismiss = { 
                    showDialog = false
                    exercicioParaEditar = null
                },
                onConfirm = { nome, series, reps, peso ->
                    if (exercicioParaEditar != null) {
                        viewModel.atualizarExercicio(
                            exercicioParaEditar!!.copy(
                                nome = nome,
                                series = series,
                                repeticoes = reps,
                                pesoCarga = peso
                            )
                        )
                    } else {
                        viewModel.adicionarExercicio(nome, series, reps, peso)
                    }
                    showDialog = false
                    exercicioParaEditar = null
                }
            )
        }
    }
}

@Composable
fun AdicionarExercicioDialog(
    exercicioParaEditar: Exercicio? = null,
    onDismiss: () -> Unit,
    onConfirm: (String, Int, Int, Double) -> Unit
) {
    var nome by remember { mutableStateOf(exercicioParaEditar?.nome ?: "") }
    var series by remember { mutableStateOf(exercicioParaEditar?.series?.toString() ?: "") }
    var reps by remember { mutableStateOf(exercicioParaEditar?.repeticoes?.toString() ?: "") }
    var peso by remember { mutableStateOf(exercicioParaEditar?.pesoCarga?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = if (exercicioParaEditar == null) "Novo Exercício" else "Editar Exercício") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome (Ex: Supino)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = series,
                        onValueChange = { novoTexto ->
                            if (novoTexto.all { it.isDigit() }) series = novoTexto
                        },
                        label = { Text("Séries") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = reps,
                        onValueChange = { novoTexto ->
                            if (novoTexto.all { it.isDigit() }) reps = novoTexto
                        },
                        label = { Text("Reps") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = peso,
                    onValueChange = { novoTexto ->
                        if (novoTexto.count { it == '.' } <= 1) {
                            peso = novoTexto
                        }
                    },
                    label = { Text("Carga (Kg)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val seriesInt = series.toIntOrNull()
                    val repsInt = reps.toIntOrNull()
                    val pesoDouble = peso.toDoubleOrNull()

                    if (nome.isNotBlank() && seriesInt != null && repsInt != null && pesoDouble != null) {
                        onConfirm(nome, seriesInt, repsInt, pesoDouble)
                    }
                }
            ) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

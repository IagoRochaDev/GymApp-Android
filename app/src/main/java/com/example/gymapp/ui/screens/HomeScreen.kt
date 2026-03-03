package com.example.gymapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.gymapp.data.local.Treino
import com.example.gymapp.ui.viewmodel.HomeViewModel

@Composable
fun TreinoCard(
    treino: Treino,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = treino.nome,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                if (treino.descricao.isNotEmpty()) {
                    Text(
                        text = treino.descricao,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Deletar", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun AdicionarTreinoDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var nome by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Novo Treino") },
        text = {
            Column {
                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome (Ex: Treino A)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = descricao,
                    onValueChange = { descricao = it },
                    label = { Text("Descrição (Opcional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nome.isNotBlank()) {
                        onConfirm(nome, descricao)
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

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onTreinoClick: (Int) -> Unit
) {
    val treinosComExercicios by viewModel.treinos.collectAsState(initial = emptyList())

    var showDialog by remember { mutableStateOf(false) }

    var treinoParaDeletar by remember { mutableStateOf<Treino?>(null) }

    Scaffold(
        topBar = { GymTopAppBar(title = "GymApp") },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Treino")
            }
        }
    ) { paddingValues ->
        if (treinosComExercicios.isEmpty()) {
            Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Nenhum treino encontrado",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Clique no + para começar!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(treinosComExercicios) { treinoComEx ->
                    TreinoCard(
                        treino = treinoComEx.treino,
                        onClick = { onTreinoClick(treinoComEx.treino.id) },
                        onDelete = { treinoParaDeletar = treinoComEx.treino }
                    )
                }
            }
            if (treinoParaDeletar != null) {
                AlertDialog(
                    onDismissRequest = { treinoParaDeletar = null },
                    title = { Text("Excluir Treino?") },
                    text = { Text("Você tem certeza que deseja excluir '${treinoParaDeletar?.nome}'? Todos os exercícios serão perdidos.") },
                    confirmButton = {
                        Button(
                            onClick = {
                                // Agora sim deleta de verdade!
                                viewModel.deletarTreino(treinoParaDeletar!!)
                                treinoParaDeletar = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Excluir")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { treinoParaDeletar = null }) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }

        if (showDialog) {
            AdicionarTreinoDialog(
                onDismiss = { showDialog = false },
                onConfirm = { nome, desc ->
                    viewModel.salvarTreino(nome, desc)
                    showDialog = false
                }
            )
        }
    }
}

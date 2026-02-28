package com.example.gymapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymapp.data.local.Treino
import com.example.gymapp.data.repository.TreinoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: TreinoRepository
) : ViewModel() {

    val treinos = repository.getTodosOsTreinos()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun salvarTreino(nome: String, descricao: String) {
        viewModelScope.launch {
            val novoTreino = Treino(nome = nome, descricao = descricao)
            repository.inserirTreino(novoTreino)
        }
    }

    fun deletarTreino(treino: Treino) {
        viewModelScope.launch {
            repository.deletarTreino(treino)
        }
    }
}
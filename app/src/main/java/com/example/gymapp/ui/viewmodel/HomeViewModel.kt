package com.example.gymapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymapp.data.local.Treino
import com.example.gymapp.data.local.TreinoComExercicios
import com.example.gymapp.data.repository.TreinoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val treinos: List<TreinoComExercicios> = emptyList(),
    val isLoading: Boolean = false
)

sealed interface HomeEvent {
    data class SalvarTreino(val nome: String, val descricao: String) : HomeEvent
    data class DeletarTreino(val treino: Treino) : HomeEvent
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: TreinoRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = repository.getTodosOsTreinos()
        .map { HomeUiState(treinos = it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeUiState(isLoading = true)
        )

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.SalvarTreino -> salvarTreino(event.nome, event.descricao)
            is HomeEvent.DeletarTreino -> deletarTreino(event.treino)
        }
    }

    private fun salvarTreino(nome: String, descricao: String) {
        viewModelScope.launch {
            val novoTreino = Treino(nome = nome, descricao = descricao)
            repository.inserirTreino(novoTreino)
        }
    }

    private fun deletarTreino(treino: Treino) {
        viewModelScope.launch {
            repository.deletarTreino(treino)
        }
    }
}

package com.example.gymapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymapp.data.local.Exercicio
import com.example.gymapp.data.local.Treino
import com.example.gymapp.data.local.TreinoComExercicios
import com.example.gymapp.data.repository.TreinoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DetalhesUiState(
    val treinoComExercicios: TreinoComExercicios? = null,
    val isLoading: Boolean = false
)

sealed interface DetalhesEvent {
    data class CarregarTreino(val id: Int) : DetalhesEvent
    data class AdicionarExercicio(val nome: String, val series: Int, val reps: Int, val peso: Double) : DetalhesEvent
    data class AtualizarExercicio(val exercicio: Exercicio) : DetalhesEvent
    data class ToggleConcluido(val exercicio: Exercicio) : DetalhesEvent
    data class DeletarExercicio(val exercicio: Exercicio) : DetalhesEvent
}

@HiltViewModel
class DetalhesTreinoViewModel @Inject constructor(
    private val repository: TreinoRepository
) : ViewModel() {

    private val _treinoIdSelecionado = MutableStateFlow<Int?>(null)

    val uiState: StateFlow<DetalhesUiState> = _treinoIdSelecionado
        .combine(repository.getTodosOsTreinos()) { id, lista ->
            DetalhesUiState(
                treinoComExercicios = lista.find { it.treino.id == id },
                isLoading = false
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DetalhesUiState(isLoading = true)
        )

    fun onEvent(event: DetalhesEvent) {
        when (event) {
            is DetalhesEvent.CarregarTreino -> carregarTreino(event.id)
            is DetalhesEvent.AdicionarExercicio -> adicionarExercicio(event.nome, event.series, event.reps, event.peso)
            is DetalhesEvent.AtualizarExercicio -> atualizarExercicio(event.exercicio)
            is DetalhesEvent.ToggleConcluido -> toggleConcluido(event.exercicio)
            is DetalhesEvent.DeletarExercicio -> deletarExercicio(event.exercicio)
        }
    }

    private fun carregarTreino(id: Int) {
        _treinoIdSelecionado.value = id
    }

    private fun adicionarExercicio(nome: String, series: Int, reps: Int, peso: Double) {
        val treinoId = _treinoIdSelecionado.value ?: return

        viewModelScope.launch {
            val novoExercicio = Exercicio(
                nome = nome,
                series = series,
                repeticoes = reps,
                pesoCarga = peso,
                treinoId = treinoId
            )
            repository.inserirExercicio(novoExercicio)
        }
    }

    private fun atualizarExercicio(exercicio: Exercicio) {
        viewModelScope.launch {
            repository.inserirExercicio(exercicio)
        }
    }

    private fun toggleConcluido(exercicio: Exercicio) {
        viewModelScope.launch {
            val exercicioAtualizado = exercicio.copy(concluido = !exercicio.concluido)
            repository.inserirExercicio(exercicioAtualizado)
        }
    }

    private fun deletarExercicio(exercicio: Exercicio) {
        viewModelScope.launch {
            repository.deletarExercicio(exercicio)
        }
    }
}

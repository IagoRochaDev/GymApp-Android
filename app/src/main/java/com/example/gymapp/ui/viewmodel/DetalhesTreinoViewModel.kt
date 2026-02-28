package com.example.gymapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymapp.data.local.Exercicio
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

@HiltViewModel
class DetalhesTreinoViewModel @Inject constructor(
    private val repository: TreinoRepository
) : ViewModel() {

    private val _treinoIdSelecionado = MutableStateFlow<Int?>(null)

    val treinoSelecionado: StateFlow<TreinoComExercicios?> = _treinoIdSelecionado
        .combine(repository.getTodosOsTreinos()) { id, lista ->
            lista.find { it.treino.id == id }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun carregarTreino(id: Int) {
        _treinoIdSelecionado.value = id
    }

    fun adicionarExercicio(nome: String, series: Int, reps: Int, peso: Double) {
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
}
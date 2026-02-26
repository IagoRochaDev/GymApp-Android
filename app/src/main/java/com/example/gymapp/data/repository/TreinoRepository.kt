package com.example.gymapp.data.repository

import com.example.gymapp.data.local.Exercicio
import com.example.gymapp.data.local.Treino
import com.example.gymapp.data.local.TreinoComExercicios
import kotlinx.coroutines.flow.Flow

interface TreinoRepository {
    suspend fun inserirTreino(treino: Treino): Long
    suspend fun inserirExercicio(exercicio: Exercicio)
    suspend fun deletarTreino(treino: Treino)
    fun getTodosOsTreinos(): Flow<List<TreinoComExercicios>>
}
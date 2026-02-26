package com.example.gymapp.data.repository

import com.example.gymapp.data.local.Exercicio
import com.example.gymapp.data.local.Treino
import com.example.gymapp.data.local.TreinoComExercicios
import com.example.gymapp.data.local.TreinoDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TreinoRepositoryImpl @Inject constructor(
    private val dao: TreinoDao
) : TreinoRepository {

    override suspend fun inserirTreino(treino: Treino): Long {
        return dao.inserirTreino(treino)
    }

    override suspend fun inserirExercicio(exercicio: Exercicio) {
        dao.inserirExercicio(exercicio)
    }

    override suspend fun deletarTreino(treino: Treino) {
        dao.deletarTreino(treino)
    }

    override fun getTodosOsTreinos(): Flow<List<TreinoComExercicios>> {
        return dao.getTodosOsTreinosComExercicios()
    }
}
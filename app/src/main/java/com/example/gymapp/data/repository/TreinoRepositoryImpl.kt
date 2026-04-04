package com.example.gymapp.data.repository

import com.example.gymapp.data.local.Exercicio
import com.example.gymapp.data.local.Treino
import com.example.gymapp.data.local.TreinoComExercicios
import com.example.gymapp.data.local.TreinoDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TreinoRepositoryImpl @Inject constructor(
    private val dao: TreinoDao
) : TreinoRepository {

    override suspend fun inserirTreino(treino: Treino): Long = withContext(Dispatchers.IO) {
        dao.inserirTreino(treino)
    }

    override suspend fun inserirExercicio(exercicio: Exercicio) = withContext(Dispatchers.IO) {
        dao.inserirExercicio(exercicio)
    }

    override suspend fun deletarTreino(treino: Treino) = withContext(Dispatchers.IO) {
        dao.deletarTreino(treino)
    }

    override suspend fun deletarExercicio(exercicio: Exercicio) = withContext(Dispatchers.IO) {
        dao.deletarExercicio(exercicio)
    }

    override fun getTodosOsTreinos(): Flow<List<TreinoComExercicios>> {
        return dao.getTodosOsTreinosComExercicios()
    }
}

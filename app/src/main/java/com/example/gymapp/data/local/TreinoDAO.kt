package com.example.gymapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TreinoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirTreino(treino: Treino): Long // Retorna o ID gerado

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirExercicio(exercicio: Exercicio)

    @Delete
    suspend fun deletarTreino(treino: Treino)

    @Transaction
    @Query("SELECT * FROM treinos")
    fun getTodosOsTreinosComExercicios(): Flow<List<TreinoComExercicios>>
}
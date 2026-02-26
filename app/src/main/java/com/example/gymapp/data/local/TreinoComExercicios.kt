package com.example.gymapp.data.local

import androidx.room.Embedded
import androidx.room.Relation

data class TreinoComExercicios(
    @Embedded
    val treino: Treino,

    @Relation(
        parentColumn = "id",
        entityColumn = "treinoId"
    )
    val exercicios: List<Exercicio>
)

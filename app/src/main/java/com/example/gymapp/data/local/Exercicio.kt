package com.example.gymapp.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "exercicios",
    foreignKeys = [
        ForeignKey(
            entity = Treino::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("treinoId"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Exercicio(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nome: String,
    val series: Int,
    val repeticoes: Int,
    val pesoCarga: Double,

    @ColumnInfo(index = true)
    val treinoId: Int
)

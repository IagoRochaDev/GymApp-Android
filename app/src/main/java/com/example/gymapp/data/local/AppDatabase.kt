package com.example.gymapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Treino::class, Exercicio::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun treinoDao(): TreinoDao
}
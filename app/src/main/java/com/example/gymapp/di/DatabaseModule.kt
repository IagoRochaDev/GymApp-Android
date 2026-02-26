package com.example.gymapp.di

import android.content.Context
import androidx.room.Room
import com.example.gymapp.data.local.AppDatabase
import com.example.gymapp.data.local.TreinoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "gymapp_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideTreinoDao(db: AppDatabase): TreinoDao {
        return db.treinoDao()
    }
}
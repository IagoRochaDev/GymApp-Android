package com.example.gymapp.di

import com.example.gymapp.data.repository.TreinoRepository
import com.example.gymapp.data.repository.TreinoRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindTreinoRepository(
        impl: TreinoRepositoryImpl
    ): TreinoRepository
}
package com.example.gymapp.data.repository

import app.cash.turbine.test
import com.example.gymapp.data.local.Exercicio
import com.example.gymapp.data.local.Treino
import com.example.gymapp.data.local.TreinoComExercicios
import com.example.gymapp.data.local.TreinoDao
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class TreinoRepositoryImplTest {

    private lateinit var repository: TreinoRepositoryImpl
    private val dao: TreinoDao = mockk()

    @Before
    fun setup() {
        repository = TreinoRepositoryImpl(dao)
    }

    @Test
    fun `inserirTreino deve chamar o dao e retornar o id`() = runTest {
        // Given
        val treino = Treino(nome = "Treino A", descricao = "Peito e Tríceps")
        val expectedId = 1L
        coEvery { dao.inserirTreino(treino) } returns expectedId

        // When
        val result = repository.inserirTreino(treino)

        // Then
        assertEquals(expectedId, result)
        coVerify(exactly = 1) { dao.inserirTreino(treino) }
    }

    @Test
    fun `inserirExercicio deve chamar o dao`() = runTest {
        // Given
        val exercicio = Exercicio(nome = "Supino", series = 3, repeticoes = 10, pesoCarga = 20.0, treinoId = 1)
        coEvery { dao.inserirExercicio(exercicio) } returns Unit

        // When
        repository.inserirExercicio(exercicio)

        // Then
        coVerify(exactly = 1) { dao.inserirExercicio(exercicio) }
    }

    @Test
    fun `deletarTreino deve chamar o dao`() = runTest {
        // Given
        val treino = Treino(id = 1, nome = "Treino A", descricao = "Peito e Tríceps")
        coEvery { dao.deletarTreino(treino) } returns Unit

        // When
        repository.deletarTreino(treino)

        // Then
        coVerify(exactly = 1) { dao.deletarTreino(treino) }
    }

    @Test
    fun `deletarExercicio deve chamar o dao`() = runTest {
        // Given
        val exercicio = Exercicio(id = 1, nome = "Supino", series = 3, repeticoes = 10, pesoCarga = 20.0, treinoId = 1)
        coEvery { dao.deletarExercicio(exercicio) } returns Unit

        // When
        repository.deletarExercicio(exercicio)

        // Then
        coVerify(exactly = 1) { dao.deletarExercicio(exercicio) }
    }

    @Test
    fun `getTodosOsTreinos deve retornar flow do dao`() = runTest {
        // Given
        val treinos = listOf(
            TreinoComExercicios(
                treino = Treino(id = 1, nome = "Treino A", descricao = "Desc"),
                exercicios = emptyList()
            )
        )
        every { dao.getTodosOsTreinosComExercicios() } returns flowOf(treinos)

        // When & Then
        repository.getTodosOsTreinos().test {
            assertEquals(treinos, awaitItem())
            awaitComplete()
        }
        coVerify(exactly = 1) { dao.getTodosOsTreinosComExercicios() }
    }
}

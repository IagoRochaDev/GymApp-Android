package com.example.gymapp.ui.viewmodel

import app.cash.turbine.test
import com.example.gymapp.data.local.Exercicio
import com.example.gymapp.data.local.Treino
import com.example.gymapp.data.local.TreinoComExercicios
import com.example.gymapp.data.repository.TreinoRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetalhesTreinoViewModelTest {

    private lateinit var viewModel: DetalhesTreinoViewModel
    private val repository: TreinoRepository = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()

    private val sampleTreino = Treino(id = 1, nome = "Treino A", descricao = "Desc A")
    private val sampleExercicio = Exercicio(id = 10, nome = "Supino", series = 3, repeticoes = 10, pesoCarga = 20.0, treinoId = 1)
    private val treinosComExercicios = listOf(
        TreinoComExercicios(treino = sampleTreino, exercicios = listOf(sampleExercicio))
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { repository.getTodosOsTreinos() } returns flowOf(treinosComExercicios)
        viewModel = DetalhesTreinoViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `treinoSelecionado deve emitir null inicialmente`() = runTest {
        viewModel.treinoSelecionado.test {
            assertEquals(null, awaitItem())
        }
    }

    @Test
    fun `carregarTreino deve atualizar treinoSelecionado`() = runTest {
        viewModel.treinoSelecionado.test {
            assertEquals(null, awaitItem()) // Inicial
            
            viewModel.carregarTreino(1)
            
            assertEquals(treinosComExercicios[0], awaitItem())
        }
    }

    @Test
    fun `adicionarExercicio deve chamar o repositorio`() = runTest {
        // Given
        viewModel.carregarTreino(1)
        coEvery { repository.inserirExercicio(any()) } returns Unit

        // When
        viewModel.adicionarExercicio("Agachamento", 3, 12, 50.0)

        // Then
        coVerify {
            repository.inserirExercicio(match {
                it.nome == "Agachamento" && it.treinoId == 1 && it.series == 3
            })
        }
    }

    @Test
    fun `toggleConcluido deve inverter o status e salvar`() = runTest {
        // Given
        coEvery { repository.inserirExercicio(any()) } returns Unit

        // When
        viewModel.toggleConcluido(sampleExercicio)

        // Then
        coVerify {
            repository.inserirExercicio(match {
                it.id == sampleExercicio.id && it.concluido == !sampleExercicio.concluido
            })
        }
    }

    @Test
    fun `deletarExercicio deve chamar o repositorio`() = runTest {
        // Given
        coEvery { repository.deletarExercicio(any()) } returns Unit

        // When
        viewModel.deletarExercicio(sampleExercicio)

        // Then
        coVerify { repository.deletarExercicio(sampleExercicio) }
    }
}

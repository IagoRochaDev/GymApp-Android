package com.example.gymapp.ui.viewmodel

import app.cash.turbine.test
import com.example.gymapp.data.local.Treino
import com.example.gymapp.data.local.TreinoComExercicios
import com.example.gymapp.data.repository.TreinoRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
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
class HomeViewModelTest {

    private lateinit var viewModel: HomeViewModel
    private val repository: TreinoRepository = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        // Mocking the initial flow for the stateIn property
        every { repository.getTodosOsTreinos() } returns flowOf(emptyList())
        viewModel = HomeViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `treinos deve refletir os dados do repositorio`() = runTest {
        // Given
        val treinosList = listOf(
            TreinoComExercicios(
                treino = Treino(id = 1, nome = "Treino 1", descricao = "Desc 1"),
                exercicios = emptyList()
            )
        )
        val repositoryFlow = MutableSharedFlow<List<TreinoComExercicios>>()
        every { repository.getTodosOsTreinos() } returns repositoryFlow
        
        // Re-instanciar para usar o novo mock flow
        val viewModel = HomeViewModel(repository)

        // When & Then
        viewModel.uiState.test {
            // Initial value from stateIn
            assertEquals(HomeUiState(isLoading = true), awaitItem())
            
            // Emit data from repository
            repositoryFlow.emit(treinosList)
            
            // Value from repository (mapped with isLoading = false)
            assertEquals(HomeUiState(treinos = treinosList, isLoading = false), awaitItem())
        }
    }

    @Test
    fun `salvarTreino deve chamar o repositorio com os dados corretos`() = runTest {
        // Given
        val nome = "Novo Treino"
        val descricao = "Nova Descricao"
        coEvery { repository.inserirTreino(any()) } returns 1L

        // When
        viewModel.onEvent(HomeEvent.SalvarTreino(nome, descricao))

        // Then
        coVerify { 
            repository.inserirTreino(match { 
                it.nome == nome && it.descricao == descricao 
            }) 
        }
    }

    @Test
    fun `deletarTreino deve chamar o repositorio`() = runTest {
        // Given
        val treino = Treino(id = 1, nome = "Treino", descricao = "Desc")
        coEvery { repository.deletarTreino(treino) } returns Unit

        // When
        viewModel.onEvent(HomeEvent.DeletarTreino(treino))

        // Then
        coVerify { repository.deletarTreino(treino) }
    }
}

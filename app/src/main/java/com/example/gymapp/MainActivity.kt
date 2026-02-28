package com.example.gymapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.gymapp.ui.screens.HomeScreen
import com.example.gymapp.ui.theme.GymAppTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gymapp.ui.screens.DetalhesTreinoScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GymAppTheme {
                val navController = rememberNavController()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") {
                            HomeScreen(
                                onTreinoClick = { treinoId ->
                                    navController.navigate("detalhes/$treinoId")
                                }
                            )
                        }

                        composable(
                            route = "detalhes/{treinoId}",
                            arguments = listOf(navArgument("treinoId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val treinoId = backStackEntry.arguments?.getInt("treinoId") ?: 0

                            DetalhesTreinoScreen(
                                treinoId = treinoId,
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}

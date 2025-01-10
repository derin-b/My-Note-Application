package com.example.noteapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.noteapplication.navigation.Navigation
import com.example.noteapplication.presentation.landing.LandingScreen
import com.example.noteapplication.presentation.NoteViewmodel
import com.example.noteapplication.ui.theme.NoteApplicationTheme
import com.example.noteapplication.utils.Constants
import org.koin.androidx.viewmodel.ext.android.getViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NoteApplicationTheme {
                val noteViewmodel =  getViewModel<NoteViewmodel>()
                val navController = rememberNavController()
                val startDestination by noteViewmodel.startDestination.collectAsState()

                Navigation(
                   navHostController = navController,
                   startDestination = startDestination,
                   noteViewmodel = noteViewmodel)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NoteApplicationTheme {
        LandingScreen({}, {})
    }
}
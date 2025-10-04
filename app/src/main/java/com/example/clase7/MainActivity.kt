package com.example.clase7

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.clase7.ui.theme.Clase7Theme
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        if (FirebaseApp.getApps(this).isEmpty()){
            FirebaseApp.initializeApp(this)
        }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            Clase7Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        MainScreens()
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreens() {

    val context = LocalContext.current

    val navController = rememberNavController()

    var initialScreen: String = stringResource(R.string.screen_login)

    val auth = Firebase.auth
    val currentUser = auth.currentUser

    if (currentUser != null){
        initialScreen = stringResource(R.string.screen_log_success)
    }

    NavHost(navController = navController, startDestination = initialScreen) {
        composable(context.getString(R.string.screen_login)) { LoginScreen(navController) }
        composable(context.getString(R.string.screen_register)) { RegisterScreen(navController) }
        composable(context.getString(R.string.screen_log_success)) { SuccessScreen(navController) }
        composable(context.getString(R.string.screen_users)) {UserScreen(navController)}
        composable(context.getString(R.string.screen_users_form)) {UsersFormScreen(navController)}
        composable("informes_screen") {
            InformesScreen(navController = navController)
        }
    }
}


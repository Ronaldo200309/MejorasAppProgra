package com.example.clase7

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController){
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()  // CAMBIADO
    val db = FirebaseFirestore.getInstance()  // CAMBIADO
    val scope = rememberCoroutineScope()

    var stateEmail by remember {mutableStateOf("")}
    var statePassword by remember {mutableStateOf("")}
    var stateConfirmPassword by remember {mutableStateOf("")}
    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

    val activity = LocalView.current.context as Activity

    Scaffold (
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = {navController.popBackStack()}){
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription= stringResource(R.string.content_description_icon_exit)
                        )
                    }
                }
            )
        }
    ){ paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 32.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Image(
                imageVector = Icons.Default.Person,
                contentDescription = stringResource(R.string.content_description_icon_person),
                modifier = Modifier.size(200.dp)
            )
            Text(
                text = stringResource(R.string.register_screen_text),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0066B3)
            )

            // Mensaje de estado
            if (message.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = message,
                    color = when {
                        message.contains("éxito") -> Color.Green
                        message.contains("Error") || message.contains("permisos") -> Color.Red
                        else -> Color.Black
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            OutlinedTextField(
                value = stateEmail,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Email,
                        contentDescription = stringResource(R.string.content_description_icon_email)
                    )
                },
                onValueChange = {stateEmail = it},
                label = {Text(stringResource(R.string.fields_email))},
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = statePassword,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = stringResource(R.string.content_description_icon_password)
                    )
                },
                onValueChange = {statePassword = it},
                label = {Text(stringResource(R.string.fields_password))},
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrect = false
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = stateConfirmPassword,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = stringResource(R.string.content_description_icon_confirm_password)
                    )
                },
                onValueChange = {stateConfirmPassword = it},
                label = {Text(stringResource(R.string.fields_confirm_password))},
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrect = false
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    // Resetear mensaje
                    message = ""

                    // Validaciones básicas
                    if (statePassword != stateConfirmPassword) {
                        Toast.makeText(activity, context.getString(R.string.register_screen_password_error), Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (statePassword.length < 6) {
                        Toast.makeText(activity, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (stateEmail.isEmpty()) {
                        Toast.makeText(activity, "El email es requerido", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    isLoading = true
                    message = "Validando permisos..."

                    // Usar coroutines para la validación
                    scope.launch {
                        try {
                            // PRIMERO: Validar si el usuario está en la lista permitida
                            val usuarioPermitido = validarUsuarioPermitido(stateEmail, db)

                            if (!usuarioPermitido) {
                                message = "Error: No tienes permisos para registrarte. Contacta al administrador."
                                isLoading = false
                                return@launch
                            }

                            // SEGUNDO: Si está permitido, crear el usuario
                            message = "Usuario autorizado. Creando cuenta..."

                            auth.createUserWithEmailAndPassword(stateEmail, statePassword)
                                .addOnCompleteListener(activity) { task ->
                                    isLoading = false
                                    if (task.isSuccessful) {
                                        message = "Registro exitoso!"
                                        Toast.makeText(activity, context.getString(R.string.register_screen_success), Toast.LENGTH_SHORT).show()

                                        // Navegar a otra pantalla después del registro exitoso
                                        navController.popBackStack()
                                    } else {
                                        message = "Error: ${task.exception?.message}"
                                        Toast.makeText(
                                            activity,
                                            "Error: ${task.exception?.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                        } catch (e: Exception) {
                            isLoading = false
                            message = "Error: ${e.message}"
                            Toast.makeText(activity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFC9252B),
                    contentColor = Color.White
                ),
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()

            ){
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(stringResource(R.string.register_screen_register_button))
                }
            }
        }
    }
}

// FUNCIÓN NUEVA: Validar si el usuario está en la lista permitida
private suspend fun validarUsuarioPermitido(email: String, db: FirebaseFirestore): Boolean {
    return try {
        // Buscar en la colección 'usuarios_permitidos' donde el email coincida
        val query = db.collection("usuarios_permitidos")
            .whereEqualTo("email", email.toLowerCase())
            .get()
            .await()

        // Si encontramos al menos un documento, el usuario está permitido
        !query.isEmpty && query.documents.isNotEmpty()
    } catch (e: Exception) {
        // Si hay error, asumimos que no está permitido
        println("Error validando usuario: ${e.message}")
        false
    }
}
package com.example.clase7

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.clase7.models.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

@Composable
fun UsersFormScreen(navController: NavController){

    val roles = listOf("coordinator", "teacher")
    val auth = Firebase.auth

    val context = LocalContext.current
    fun saveUser(db: FirebaseFirestore, user: User, navController: NavController) {
        db.collection(USERS_COLLECTION)
            .add(user)
            .addOnSuccessListener { documentReference ->
                navController.navigate(context.getString(R.string.screen_users))
            }
    }
    var stateEmail by remember {mutableStateOf("")}
    var stateRoles by remember {mutableStateOf("")}

    var stateMessage by remember {mutableStateOf("")}

    var selectedOptions = remember {mutableStateListOf<String>()}

    val db = Firebase.firestore


    Column(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement =Arrangement.Center
    ){


        Spacer(modifier = Modifier.height(10.dp))
        Text(stringResource(R.string.user_screen_new_user))
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
//            supportingText = {
//                if (emailMessage.isNotEmpty()){
//                    Text(
//                        text=emailMessage,
//                        color=Color.Red
//                    )
//                }
//            }
        )
        Spacer(modifier = Modifier.height(10.dp))

        Column {
            roles.forEach{ option ->
                Row(
                   Modifier.fillMaxWidth()
                       .selectable(
                           selected = selectedOptions.contains(option),
                           onClick = {
                               if (selectedOptions.contains(option)) {
                                   selectedOptions.remove(option)
                               } else {
                                   selectedOptions.add(option)
                               }
                           }
                       ).padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Checkbox(
                        checked = selectedOptions.contains(option),
                        onCheckedChange = null // null recommended for accessibility with selectable modifier
                    )
                    Text(
                        text = option,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = {
                val roles = selectedOptions.joinToString(",")
                val user = User("", stateEmail, roles)
                saveUser(db, user, navController)
//
//                auth.createUserWithEmailAndPassword()
//
//                auth.createUserWithEmailAndPassword(stateEmail, statePassword)
//                    .addOnCompleteListener(activity) { task ->
//                        if (task.isSuccessful) {
//                            Toast.makeText(activity, context.getString(R.string.register_screen_success), Toast.LENGTH_SHORT)
//                                .show()
//                        } else {
//                            Toast.makeText(
//                                activity,
//                                "Error: ${task.exception?.message}",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//                    }


            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFC9252B),
                contentColor = Color.White
            )
        ){
            Text(stringResource(R.string.user_form_screen_save))
        }

        IconButton(onClick = {navController.popBackStack()}){
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription= stringResource(R.string.content_description_icon_exit)
            )
        }

        Text(
            text = stateMessage
        )
    }
}
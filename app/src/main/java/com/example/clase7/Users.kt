package com.example.clase7


import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.example.clase7.models.User
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject


const val USERS_COLLECTION = "users"

suspend fun getUsers (db: FirebaseFirestore) : List<User> {
    val snapshot = db.collection(USERS_COLLECTION)
        .get()
        .await()

    val usersList = snapshot.documents.map{doc ->
        doc.toObject<User>()?.copy(id = doc.id, email = doc.get("email").toString(), doc.get("roles").toString())
          ?: User()
    }
    return usersList
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(navController: NavController) {
    val context = LocalContext.current
    val db = Firebase.firestore
    var users by remember {mutableStateOf(emptyList<User>())}
    var isLoading by remember {mutableStateOf(false)}

    LaunchedEffect(Unit) {
        isLoading = true
        users = getUsers(db)
        isLoading = false
    }

    Scaffold (
        topBar = {
            TopAppBar(
                title = {stringResource(R.string.user_screen_tittle)},
                navigationIcon = {
                    IconButton(onClick = {navController.popBackStack()}){
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription= stringResource(R.string.content_description_icon_exit)
                        )
                    }
                }
            )
        }
    ){ innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,

        )
        {

            Spacer(modifier = Modifier.padding(8.dp))
            Text(stringResource(R.string.user_screen_tittle))
            if (isLoading){
                CircularProgressIndicator()
            }
            else{
                LazyColumn{
                    for (user in users){
                        item(user.id){
                            Card(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth()
                                    .clickable(onClick= {
                                        navController
                                            .navigate(context.getString(R.string.screen_users_form))
                                    })
                                ,
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.Absolute.SpaceBetween
                                )
                                {
                                    Text("Email: " + user.email , modifier = Modifier.weight(1f))
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text("Roles: " + user.roles , modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
                    .padding(16.dp)
            )
            {
             IconButton(onClick = {navController.navigate(context.getString(R.string.screen_users_form))})
                {
                  Icon(
                     imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(R.string.content_description_icon_add),
                        modifier = Modifier.size(50.dp),

                      )
                }
            }
        }
    }
}
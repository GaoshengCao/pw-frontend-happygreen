package com.example.frontend_happygreen


import android.icu.text.CaseMap.Title
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.frontend_happygreen.ui.theme.FrontendhappygreenTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FrontendhappygreenTheme {
                val navController = rememberNavController()
                Surface (modifier = Modifier.fillMaxSize()){
                    NavHost(
                        navController = navController,
                        startDestination = "first"
                    ) {
                        composable("first"){ FirstPage((navController))}
                        composable("login"){ LoginPage((navController)) }
                        composable("register"){ RegisterPage((navController))}
//                        composable("createGroup"){ CreateGroupPage((navController))}
//                        composable("enterGroup"){ EnterGroupPage((navController))}
                        composable("group/{name}"){ backStackEntry ->
                            val name = backStackEntry.arguments?.getString("name") ?: "???"
                        GroupPage(navController,name)}
//                        composable("addPost"){ AddPostPage((navController))}
//                        composable("comment"){ CommentPage((navController))}
//                        composable("groupmap"){ GroupMapPage((navController))}


                        composable("game") { GamePage(navController) }
                        composable("camera") { CameraPage(navController) }
                        composable("home") { HomePage(navController) }
                        composable("profile") { UserPage(navController) }
                        composable("options") { OptionsPage(navController) }
                    }
                }
            }
        }
    }
}

//Barra Con Titolo
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderBar(navController: NavHostController, title:String) {
    TopAppBar(
        title = {
            Text(
                text = title,
                fontSize = 20.sp
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(99, 169, 177)),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary

        )
    )
}

//Barra Di Navgazione
@Composable
fun BottomNavBar(navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color(99, 169, 177)),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val navItems = listOf(
            Triple("game", R.drawable.controller, "Game"),
            Triple("camera", R.drawable.camera, "Camera"),
            Triple("home", R.drawable.home, "Home"),
            Triple("user", R.drawable.user, "User"),
            Triple("options", R.drawable.menu, "Options")
        )

        navItems.forEach { (destination, icon, label) ->
            NavBarButton(destination, icon, label, navController)
        }
    }
}

//Singoli Bottooni In Basso
@Composable
fun NavBarButton(destination: String, iconResId: Int, label: String, navController: NavHostController) {
    Button(
        onClick = { navController.navigate(destination) },
        shape = RoundedCornerShape(5.dp),
        modifier = Modifier
            .padding(1.dp)
            .background(Color(99, 169, 177)),

    ) {
        Image(
            painter = painterResource(iconResId),
            contentDescription = label,
            modifier = Modifier
                .size(24.dp) // Adjust size of the icon
                .padding(2.dp) // Optional: Add padding inside the button around the image
        )
    }
}


@Composable
fun CenteredContent(paddingValues: PaddingValues, text: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(Color(79, 149, 157)),
        contentAlignment = Alignment.Center
    ) {
        Text(text)
    }
}

@Composable
fun HomePage(navController: NavHostController) {

    //Variabili Per Testare
    val groups = listOf("Family", "Work", "Friends", "Study")

    Scaffold(
        topBar = { HeaderBar(navController, "HappyGreen") },
        bottomBar = { BottomNavBar(navController) }
    ) { paddingValues ->
        // Main content inside the Scaffold, using Column to organize UI elements vertically
        Column(
            modifier = Modifier
                .padding(paddingValues) // Ensure the content respects Scaffold's padding
                .fillMaxSize(), // Make sure the column takes up the available space
            horizontalAlignment = Alignment.CenterHorizontally, // Center contents horizontally
            verticalArrangement = Arrangement.Center // Center contents vertically
        ) {

            groups.forEach { group ->
                ElementGroup(navController = navController, name = group)
            }

            // Add Navigation Buttons with vertical spacing between them
            NavigationButton("Crea Gruppo", "createGroup", navController)
            Spacer(modifier = Modifier.height(16.dp)) // Add spacing between buttons
            NavigationButton("Unisciti A Un Gruppo", "enterGroup", navController)
        }
    }
}

//TODO
@Composable
fun ElementGroup(navController: NavHostController, name : String){
    Box(modifier = Modifier.clickable {
        navController.navigate("group/$name");
    }){
        Text(name)
    }
}

@Composable
fun GamePage(navController: NavHostController) {
    Scaffold(
        topBar = { HeaderBar(navController,"Happy Green") },
        bottomBar = { BottomNavBar(navController) }
    ) { paddingValues ->
        CenteredContent(paddingValues, "Giochi")
    }
}

@Composable
fun CameraPage(navController: NavHostController) {
    Scaffold(
        topBar = { HeaderBar(navController,"Happy Green") },
        bottomBar = { BottomNavBar(navController) }
    ) { paddingValues ->
        CenteredContent(paddingValues, "Camera")
    }
}

@Composable
fun UserPage(navController: NavHostController) {
    Scaffold(
        topBar = { HeaderBar(navController,"Happy Green") },
        bottomBar = { BottomNavBar(navController) }
    ) { paddingValues ->
        CenteredContent(paddingValues, "Utente")
    }
}

@Composable
fun OptionsPage(navController: NavHostController) {
    Scaffold(
        topBar = { HeaderBar(navController,"Happy Green") },
        bottomBar = { BottomNavBar(navController) }
    ) { paddingValues ->
        CenteredContent(paddingValues, "Opzioni")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FirstPage(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(79, 149, 157))
    ) {
        TopAppBar(
            title = {},
            navigationIcon = {
                IconButton(onClick = {}, enabled = false) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.Transparent)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(79, 149, 157)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top

        ) {
            Spacer(modifier = Modifier.height(100.dp))
            Text(
                text = "Happy Green",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(200.dp))

            NavigationButton(
                text = "Login",
                destination = "login",
                navController = navController
            )
            NavigationButton(
                text = "Register",
                destination = "register",
                navController = navController
            )
        }
    }
}


@Composable
fun NavigationButton(text: String, destination: String, navController: NavHostController) {
    Button(
        onClick = { navController.navigate(destination) },
        shape = RoundedCornerShape(5.dp),
        modifier = Modifier.fillMaxWidth(0.4f) // 40% width of screen
    ) {
        Text(text = text)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginPage(navController: NavHostController) {
    val coroutineScope = rememberCoroutineScope()
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(79, 149, 157))
    ) {
        TopAppBar(
            title = {},
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            ),
            modifier = Modifier.fillMaxWidth()
        )

        // Content container with centered alignment and full width
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)  // fill remaining height
                .padding(horizontal = 0.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(100.dp))
            Text(
                text = "Happy Green",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(50.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(0.8f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(0.8f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {


//                    val apiService = RetrofitInstance.api
                    val apiService = RetrofitInstance.create("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0b2tlbl90eXBlIjoiYWNjZXNzIiwiZXhwIjoxNzQ3NDk4OTExLCJpYXQiOjE3NDc0OTg2MTEsImp0aSI6IjEyMjQ2NGE4NzExMTQzYTRiNDllNGFmMjA3MTNmYzMwIiwidXNlcl9pZCI6MX0.s9kHrkQ0qLTlKrZJ64TYyrLuuMFhKVPOBe0aJnkdcyw")
                    coroutineScope.launch {
                        val nome = getUsernameById(apiService, 1)
                    }


                },
                shape = RoundedCornerShape(5.dp),
                modifier = Modifier.fillMaxWidth(0.4f),
                enabled = username.isNotBlank() && password.isNotBlank()
            ) {
                Text("Login")
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterPage(navController: NavHostController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(79, 149, 157))
    ) {
        TopAppBar(
            title = {},
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            modifier = Modifier.fillMaxWidth()
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(100.dp))

            Text(
                text = "Happy Green",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(50.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(0.8f),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(0.8f),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
                )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(0.8f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { /* Handle registration */ },
                shape = RoundedCornerShape(5.dp),
                modifier = Modifier.fillMaxWidth(0.4f),
                enabled = username.isNotBlank() && password.isNotBlank()
            ) {
                Text("Register")
            }
        }
    }
}



//TODO
//Controllo Sessione se utente è Acceduto
//Controllo su utente fa parte di gruppi
//
//Pagine della NavBar





//

//
//@Composable
//fun CreateGroupPage(navController: NavHostController) {
//
//    //TextBox Nome Gruppo
//    //Bottone Invia
//
//
//}
//
//@Composable
//fun EnterGroupPage(navController: NavHostController) {
//
//    //TextBox Codice Gruppo
//    //Bottone Invia
//
//}
//
@Composable
fun GroupPage(navController: NavHostController, nome : String) {

    //Barra sopra Nome Gruppo

    //Contenuto Tutti i Post

    //Botton Bar
}
//
//@Composable
//fun AddPostPage(navController: NavHostController) {
//
//    //TextBox Descrizione Post
//    //Carica Immagine
//    //Inserimento Luogo
//
//}
//
//@Composable
//fun CommentPage(navController: NavHostController) {
//
//    //Lista Commenti:
//    //Nome Utente
//    //Commento
//
//}
//
//@Composable
//fun GroupMapPage(navController: NavHostController) {
//
//    //Mappa con Tutte le Punte Di dove Sono I post
//}
//
//

package com.example.frontend_happygreen

import android.icu.text.CaseMap.Title
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import android.view.WindowInsets
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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
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
        WindowCompat.setDecorFitsSystemWindows(window, true)
        setContent {
            FrontendhappygreenTheme {
                val navController = rememberNavController()
                Surface (modifier = Modifier.fillMaxSize()
                ){
                    NavHost(
                        navController = navController,
                        startDestination = "home"
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
                        composable("user") { UserPage(navController) }
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
                fontSize = 30.sp,
                fontWeight = FontWeight.Black
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



//
// 3 (HOME)
//
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
                .verticalScroll(rememberScrollState())
                .fillMaxSize(), // Make sure the column takes up the available space

            horizontalAlignment = Alignment.CenterHorizontally, // Center contents horizontally
            verticalArrangement = Arrangement.Top // Center contents vertically

        ) {

            groups.forEach { group ->
                ElementGroup(navController = navController, name = group)
            }
            // Add Navigation Buttons with vertical spacing between them
            Spacer(modifier = Modifier.height(16.dp))
            NavigationButton("Crea Gruppo", "createGroup", navController)
            Spacer(modifier = Modifier.height(4.dp))
            NavigationButton("Unisciti Gruppo", "enterGroup", navController)
        }
    }
}

@Composable
fun ElementGroup(navController: NavHostController, name : String){
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .background(Color.LightGray)
        .clickable {
        navController.navigate("group/$name");
    },
    ){
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = name,
                style = TextStyle(fontSize = 30.sp, fontWeight = FontWeight.SemiBold),

                modifier = Modifier.padding(start = 8.dp, top = 8.dp, bottom = 8.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    navController.navigate("group/$name")
                },
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Go to group"
                )
            }
        }
    }
}

//
// 7 (Group Page)
//
@Composable
fun GroupPage(navController: NavHostController, nome : String) {
    //Variabile per testare
    val posts = listOf("Post 1", "Post 2", "Post 3", "Post 4")

    Scaffold(
        topBar = { GroupHeaderBar(navController, nome) },
        bottomBar = { BottomNavBar(navController) }
    ) { paddingValues ->
        // Main content inside the Scaffold, using Column to organize UI elements vertically
        Column(
            modifier = Modifier
                .padding(paddingValues) // Ensure the content respects Scaffold's padding
                .verticalScroll(rememberScrollState())
                .fillMaxSize(), // Make sure the column takes up the available space

            horizontalAlignment = Alignment.CenterHorizontally, // Center contents horizontally
            verticalArrangement = Arrangement.Top // Center contents vertically

        ) {

            posts.forEach { post ->
                ElementPost(navController = navController, name = post)
            }
        }
    }
}
//TODO
@Composable
fun ElementPost(navController: NavHostController, name: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.LightGray)
            .clickable {
                navController.navigate("group/$name")
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Title
            Text(
                text = name,
                style = TextStyle(fontSize = 26.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Image placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color.DarkGray), // Replace with Image if needed
                contentAlignment = Alignment.Center
            ) {
//                Image()
            }

            // Text + Button Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Description or preview text
                Text(
                    text = "Group preview or description goes here...",
                    modifier = Modifier.weight(1f),
                    style = TextStyle(fontSize = 16.sp)
                )

                // Button on the left side
                Button(
                    onClick = {
                        navController.navigate("group/$name")
                    },
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .height(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Go to group"
                    )
                }
            }
        }
    }
}


//TODO NAVIGAZIONE ALTRE PAGINE
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupHeaderBar(navController: NavHostController, title: String) {
    TopAppBar(
        title = {
            Text(
                text = title,
                fontSize = 30.sp,
                fontWeight = FontWeight.Black,
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(99, 169, 177))
            .clickable { /*TODO NAVIGATE (MapPage)*/ },
        actions = {
            IconButton(onClick = {
                // TODO: Add navigation or action for the plus button
                // Example: navController.navigate("addPost")
            }) {
                Icon(
                    imageVector = Icons.Filled.AddCircle,
                    contentDescription = "Add",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(35.dp)
                )

            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}



//TODO
@Composable
fun GamePage(navController: NavHostController) {
    Scaffold(
        topBar = { HeaderBar(navController,"Happy Green") },
        bottomBar = { BottomNavBar(navController) }
    ) { paddingValues ->
        CenteredContent(paddingValues, "Giochi")
    }
}

//TODO
@Composable
fun CameraPage(navController: NavHostController) {
    Scaffold(
        topBar = { HeaderBar(navController,"Happy Green") },
        bottomBar = { BottomNavBar(navController) }
    ) { paddingValues ->
        CenteredContent(paddingValues, "Camera")
    }
}

//TODO
@Composable
fun UserPage(navController: NavHostController) {
    Scaffold(
        topBar = { HeaderBar(navController,"Happy Green") },
        bottomBar = { BottomNavBar(navController) }
    ) { paddingValues ->
        CenteredContent(paddingValues, "User")
    }
}

//TODO
@Composable
fun OptionsPage(navController: NavHostController) {
    Scaffold(
        topBar = { HeaderBar(navController,"Happy Green") },
        bottomBar = { BottomNavBar(navController) }
    ) { paddingValues ->
        CenteredContent(paddingValues, "Opzioni")
    }
}

//Pagina quando apri l'app per la prima volta
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

//Semplici Bottoni di Navigazione
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

//Pagina Per effettuare il Login
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginPage(navController: NavHostController) {

    //  Per API
//    val coroutineScope = rememberCoroutineScope()
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

//Riguardare Quando Fare API request
//                    val apiService = RetrofitInstance.api
//                    val apiService = RetrofitInstance.create("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0b2tlbl90eXBlIjoiYWNjZXNzIiwiZXhwIjoxNzQ3NDk4OTExLCJpYXQiOjE3NDc0OTg2MTEsImp0aSI6IjEyMjQ2NGE4NzExMTQzYTRiNDllNGFmMjA3MTNmYzMwIiwidXNlcl9pZCI6MX0.s9kHrkQ0qLTlKrZJ64TYyrLuuMFhKVPOBe0aJnkdcyw")
//                    coroutineScope.launch {
//                        val nome = getUsernameById(apiService, 1)
//                    }
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

//Pagina Per effettuare la registrazione
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
//@Composable
//fun CreateGroupPage(navController: NavHostController) {
//
//    //TextBox Nome Gruppo
//    //Bottone Invia
//
//
//}
//TODO
//@Composable
//fun EnterGroupPage(navController: NavHostController) {
//
//    //TextBox Codice Gruppo
//    //Bottone Invia
//
//}
//

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

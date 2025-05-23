package com.example.frontend_happygreen

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.icu.text.CaseMap.Title
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import android.view.WindowInsets
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import android.Manifest
import android.content.pm.PackageManager
import android.provider.MediaStore.Audio.Genres.Members
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.traceEventEnd
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import coil.compose.rememberAsyncImagePainter
import com.example.frontend_happygreen.ui.theme.FrontendhappygreenTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.graphics.Color as AndroidColor
import androidx.compose.ui.graphics.Color as ComposeColor

fun String.toComposeColor(): ComposeColor = ComposeColor(AndroidColor.parseColor(this))

object SecureStorage {

    private fun getSharedPrefs(context: Context): SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            "secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun saveToken(context: Context, token: String?) {
        getSharedPrefs(context)
            .edit()
            .putString("auth_token", token)
            .apply()
    }
    fun saveUser(context: Context, id : Int) {
        getSharedPrefs(context)
            .edit()
            .putInt("User_ID", id)
            .apply()
    }

    fun getToken(context: Context): String? {
        return getSharedPrefs(context).getString("auth_token", null)
    }
    fun getUser(context: Context): Int {
        return getSharedPrefs(context).getInt("User_ID", 0)
    }

    fun clearToken(context: Context) {
        getSharedPrefs(context).edit().remove("auth_token").apply()
    }
    fun clearUser(context: Context) {
        getSharedPrefs(context).edit().remove("User_ID").apply()
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, true)
        setContent {
            FrontendhappygreenTheme {
                val navController = rememberNavController()
                val context = LocalContext.current.applicationContext
                var page = "splash_screen"



                Surface (modifier = Modifier.fillMaxSize()
                ){
                    NavHost(
                        navController = navController,
                        startDestination = page,
                        modifier = Modifier.navigationBarsPadding()
                    ) {
                        composable("splash_screen") { SplashScreen(navController = navController) }
                        composable("first"){ FirstPage((navController))} // 1
                        composable("login"){ LoginPage((navController)) } // 2
                        composable("register"){ RegisterPage((navController))} // 3
                        composable("home") { HomePage(navController) } // 4
                        composable("createGroup"){ CreateGroupPage((navController))} // 5
                        composable("enterGroup"){ JoinGroupPage((navController))} // 6
                        composable("mapPage/{name}"){ backStackEntry ->
                            val name = backStackEntry.arguments?.getString("name") ?: "???"
                            GroupMapPage(navController,name)
                        } // 8
                        composable("group/{name}"){ backStackEntry ->
                            val name = backStackEntry.arguments?.getString("name") ?: "???"
                        GroupPage(navController,name)} // 7
                       composable("addPost/{name}"){ backStackEntry ->
                           val name = backStackEntry.arguments?.getString("name") ?: "???"
                           AddPostPage(navController,name)} // 9
                        composable(
                            "comment/{id}",
                            arguments = listOf(navArgument("id") {
                                type = NavType.IntType
                            })
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getInt("id") ?: 0
                            CommentPage(navController, id)
                        } // 10
                        composable("game") { GamePage(navController) }
                        composable("quizpage"){
                            val coroutineScope = rememberCoroutineScope()
                            var quiz by remember { mutableStateOf<List<QuizQuestion>>(emptyList()) }
                            coroutineScope.launch {
                                val api = RetrofitInstance.api
                                quiz = getFiveQuizQuestion(api)
                            }
                            QuizPage(navController,quiz)}
                        composable("resultpage/{result}") { backStackEntry ->
                            val result = backStackEntry.arguments?.getString("result")?.toIntOrNull() ?: -1
                            QuizResultPage(result, navController)
                        }
                        composable("camera") { CameraPage(navController) }
                        composable("user") { UserPage(navController) }
                        composable("options") { OptionsPage(navController) }
                    }
                }
            }
        }
    }
}

@Composable
fun SplashScreen(navController: NavController) {
    val scale = remember {
        androidx.compose.animation.core.Animatable(0f)
    }
    val context = LocalContext.current.applicationContext

    // AnimationEffect
    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 0.7f,
            animationSpec = tween(
                durationMillis = 800,
                easing = {
                    OvershootInterpolator(4f).getInterpolation(it)
                })
        )
        delay(3000L)
        if (SecureStorage.getToken(context) == null){
            navController.navigate("first")
        }else{
            navController.navigate("home")
        }

    }

    // Image
    Box(contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
            .background("#4CAF50".toComposeColor())
    ) {
        Image(painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier.scale(scale.value))
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
// 1(FirstPage)
//
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

//
// 2 (LoginPage)
//
//Pagina Per effettuare il Login
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginPage(navController: NavHostController) {
    //  Per API
    val coroutineScope = rememberCoroutineScope()
    //Salvataggio Token
    val context = LocalContext.current.applicationContext

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
                    coroutineScope.launch {
                        val api = RetrofitInstance.api
                        val apiToken = loginUser(api, username, password)
                        SecureStorage.saveToken(context, apiToken)
                        val id = getId(api,username)
                        SecureStorage.saveUser(context,id)
                        navController.navigate("home")
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

//
// 3(RegisterPage)
//
//Pagina Per effettuare la registrazione
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterPage(navController: NavHostController) {
    //  Per API
    val coroutineScope = rememberCoroutineScope()
    //Salvataggio Token
    val context = LocalContext.current.applicationContext
    val token = remember { mutableStateOf<String?>(null) }

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var risultato by remember { mutableStateOf("") }

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


            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        //Crea L'utente
                        val apiService = RetrofitInstance.api
                        val result = registerUser(apiService, username, password)

                        val firstWord = result?.split(" ")?.firstOrNull()

                        if (firstWord == "Registered") {
                            // Success
                            val id = getId(apiService,username)
                            SecureStorage.saveUser(context,id)
                            SecureStorage.saveToken(context, loginUser(apiService, username, password))
                            navController.navigate("home")
                        } else {
                            risultato = "Registrazione Fallita, Nome Utente Già Esistente"
                        }

                    }
                },
                shape = RoundedCornerShape(5.dp),
                modifier = Modifier.fillMaxWidth(0.4f),
                enabled = username.isNotBlank() && password.isNotBlank()
            ) {
                Text("Register")
            }
            Text(risultato)
        }
    }
}

//
// 4 (HOME)
//
@Composable
fun HomePage(navController: NavHostController) {
    //Variabili Per Testare
    val coroutineScope = rememberCoroutineScope()
    var groups by remember { mutableStateOf<List<Group>?>(emptyList()) }
    val context = LocalContext.current.applicationContext

    val userId = SecureStorage.getUser(context)

    LaunchedEffect(userId) {
        val api = RetrofitInstance.api
        groups = getGroupsByUserID(api, userId)
    }

    Scaffold(
        topBar = { HeaderBar(navController, "Happy Green") },
        bottomBar = { BottomNavBar(navController) }
    ) { paddingValues ->
        // Main content inside the Scaffold, using Column to organize UI elements vertically
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // List of groups with dividers
            groups?.forEachIndexed { index, group ->
                ElementGroup(navController, group)
                if (index != groups?.lastIndex) {
                    Divider(
                        color = Color.LightGray,
                        thickness = 1.dp,
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            NavigationButton("Crea Gruppo", "createGroup", navController)
            Spacer(modifier = Modifier.height(4.dp))
            NavigationButton("Unisciti Gruppo", "enterGroup", navController)
        }
    }
}

@Composable
fun ElementGroup(navController: NavHostController, group: Group){
    Box(modifier = Modifier
        .fillMaxWidth()
        .clickable {
        navController.navigate("group/${group.name}");
    },
    ){
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = group.name,
                style = TextStyle(fontSize = 30.sp, fontWeight = FontWeight.SemiBold),

                modifier = Modifier.padding(start = 8.dp, top = 8.dp, bottom = 8.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

//
// 5 (Create Group)
//
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGroupPage(navController: NavHostController) {
    //  Per API
    val coroutineScope = rememberCoroutineScope()
    //Salvataggio Token
    val context = LocalContext.current.applicationContext
    var username by remember { mutableStateOf("") }
    var risultato by remember { mutableStateOf("") }

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
                label = { Text("Nome Gruppo") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(0.8f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    coroutineScope.launch {

                        val api = RetrofitInstance.api
                        val id = SecureStorage.getUser(context)
                        val responce = createGroup(api,id,username)


                        val firstWord = responce.split(" ")?.firstOrNull()

                        if (firstWord == "Created"){
                            navController.navigate("home")
                        }else{
                            risultato = responce
                        }
                    }
                },
                shape = RoundedCornerShape(5.dp),
                modifier = Modifier.fillMaxWidth(0.4f),
                enabled = username.isNotBlank()
            ) {
                Text("Crea Gruppo")
            }
            Text(risultato)
        }
    }
}

//
// 6 (Join Group)
//
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoinGroupPage(navController: NavHostController) {
    //  Per API
    val coroutineScope = rememberCoroutineScope()
    //Salvataggio Token
    val context = LocalContext.current.applicationContext

    var username by remember { mutableStateOf("") }

    var risultato by remember { mutableStateOf("") }

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
                label = { Text("ID Gruppo") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(0.8f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    coroutineScope.launch {

                        val api = RetrofitInstance.api
                        val id = SecureStorage.getUser(context)
                        val responce = joinGroup(api,id,username.toInt())

                        val firstWord = responce.split(" ")?.firstOrNull()

                        if (firstWord == "Joined"){
                            navController.navigate("home")
                        }else{
                            risultato = responce
                        }
                    }
                },
                shape = RoundedCornerShape(5.dp),
                modifier = Modifier.fillMaxWidth(0.4f),
                enabled = username.isNotBlank()
            ) {
                Text("Join")
            }
            Text(risultato)
        }
    }
}

//
// 7 (Group Page)
//
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun GroupPage(navController: NavHostController, nome: String) {
    val coroutineScope = rememberCoroutineScope()
    var posts by remember { mutableStateOf<List<Post>>(emptyList()) }
    val context = LocalContext.current.applicationContext
    val userId = SecureStorage.getUser(context)

    LaunchedEffect(userId) {
        val api = RetrofitInstance.api
        posts = getPostByGroupName(api, nome) ?: emptyList()
    }

    Scaffold(
        topBar = { GroupHeaderBar(navController, nome) },
        bottomBar = { BottomNavBar(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            posts.forEachIndexed { index, post ->
                ElementPost(navController, post)
                if (index != posts.lastIndex) {
                    Divider(
                        color = Color.LightGray,
                        thickness = 1.dp,
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun ElementPost(navController: NavHostController, post: Post) {
    val coroutineScope = rememberCoroutineScope()
    var author by remember { mutableStateOf("") }

    // Direct mapping
    val text = post.text
    val imageLink = post.image?.replaceFirst("http://", "https://") ?: ""

    val lat = post.location_lat ?: 0.0
    val lng = post.location_lng ?: 0.0


    LaunchedEffect(post.author) {
        val api = RetrofitInstance.api
        author = getUsernameById(api, post.author) ?: "Unknown"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color(0xFFEFEFEF), RoundedCornerShape(10.dp))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Author name
            Text(
                text = author,
                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Post image
            AsyncImage(
                model = imageLink,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Fit
            )



            Spacer(modifier = Modifier.height(8.dp))

            // Description
            Text(
                text = text,
                style = TextStyle(fontSize = 16.sp),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Location (optional)
            if (lat != 0.0 || lng != 0.0) {
                Text(
                    text = "📍 $lat, $lng",
                    style = TextStyle(fontSize = 12.sp, color = Color.Gray)
                )
            }

            // Comment button
            Button(
                onClick = {
                    navController.navigate("comment/${post.id}")
                },
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Go to comments"
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Commenta")
            }
        }
    }
}
//
// 8(MapPage)
//
@OptIn(ExperimentalMaterial3Api::class, MapsComposeExperimentalApi::class)
@Composable
fun GroupMapPage(navController: NavHostController, nomeGruppo: String) {
    val coroutineScope = rememberCoroutineScope()
    var posts by remember { mutableStateOf<List<Post>>(emptyList()) }
    val context = LocalContext.current.applicationContext
    val userId = SecureStorage.getUser(context)

    var risultato by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var members by remember { mutableStateOf<List<User>>(emptyList()) }
    var groupID by remember { mutableStateOf(0) }
    LaunchedEffect(userId) {
        val api = RetrofitInstance.api
        groupID = getIDGroup(api,nomeGruppo)
        posts = getPostByGroupName(api, nomeGruppo) ?: emptyList()
        members = getMembersByGroupName(api, nomeGruppo)
    }

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

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 0.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(
                    posts.firstOrNull()?.let {
                        LatLng(it.location_lat ?: 0.0, it.location_lng ?: 0.0)
                    } ?: LatLng(0.0, 0.0), 10f
                )
            }

            GoogleMap(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                cameraPositionState = cameraPositionState
            ) {
                posts.forEach { post ->
                    val lat = post.location_lat
                    val lng = post.location_lng

                    if (lat != null && lng != null) {
                        Marker(
                            state = MarkerState(position = LatLng(lat, lng)),
                            title = post.author.toString(),
                            snippet = post.text
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "ID Gruppo : $groupID",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Membri del Gruppo",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )

            Column {
                for (user in members) {
                    Text(
                        text = user.username,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        color = Color.White
                    )
                }
            }

        }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        // Sezione da non toccare
//                        val api = RetrofitInstance.api
//                        val id = SecureStorage.getUser(context)
//                        val responce = joinGroup(api,id,username.toInt())
//
//                        val firstWord = responce.split(" ")?.firstOrNull()
//
//                        if (firstWord == "Joined"){
//                            navController.navigate("home")
//                        }else{
//                            risultato = responce
//                        }
                    }
                },
                shape = RoundedCornerShape(5.dp),
                modifier = Modifier.fillMaxWidth(0.4f),
                enabled = username.isNotBlank()
            ) {
                Text("Esci Gruppo")
            }

            Text(risultato, color = Color.White)
        }
    }



//
// 9(Create Post)
//
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AddPostPage(navController: NavHostController, groupName: String) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var description by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var location by remember { mutableStateOf<LatLng?>(null) }
    var resultText by remember { mutableStateOf("") }

    // Location permission state
    val permissionState = rememberPermissionState(
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    // Image picker launcher
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageUri = uri
    }

    // Fused location client
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    // Get user location if permission is granted
    LaunchedEffect(permissionState.status.isGranted) {
        if (permissionState.status.isGranted) {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            if (hasPermission) {
                fusedLocationClient.lastLocation.addOnSuccessListener { locationResult ->
                    locationResult?.let {
                        location = LatLng(it.latitude, it.longitude)
                    }
                }
            }
        } else {
            permissionState.launchPermissionRequest()
        }
    }

    // Camera position state, updated with selected location or default to Rome
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(location ?: LatLng(41.9028, 12.4964), 6f)
    }

    // When location changes, move camera to new position
    LaunchedEffect(location) {
        location?.let {
            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(it, 15f))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(79, 149, 157))
            .verticalScroll(rememberScrollState())
    ) {
        TopAppBar(
            title = {},
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Aggiungi Post a $groupName",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descrizione") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            singleLine = false,
            maxLines = 3
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { launcher.launch("image/*") },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Seleziona Immagine")
        }

        imageUri?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = null,
                modifier = Modifier
                    .padding(16.dp)
                    .height(200.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        location?.let {
            Text(
                text = "Posizione selezionata: ${it.latitude}, ${it.longitude}",
                color = Color.White,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        Text(
            text = "Tocca la mappa per selezionare un'altra posizione:",
            color = Color.White,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // --- Google Map Composable ---
        GoogleMap(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(horizontal = 16.dp),
            cameraPositionState = cameraPositionState,
            onMapClick = { latLng ->
                location = latLng
            }
        ) {
            location?.let {
                Marker(
                    state = MarkerState(position = it),
                    title = "Posizione selezionata"
                )
            }
        }
        // --- end map ---

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {

                coroutineScope.launch {
                    val api = RetrofitInstance.api

                    if (imageUri != null && location != null && description.isNotBlank()) {
                        val postData = PostData(
                            groupId = getIDGroup(api,groupName),       // pass actual group ID if available
                            authorId = SecureStorage.getUser(context),      // pass actual user ID if available
                            text = description,
                            locationLat = location?.latitude,
                            locationLng = location?.longitude,
                            imageUri = imageUri!!
                        )

                        resultText = createPost(api, context, postData)


                    } else {
                        resultText = "Seleziona immagine, posizione e inserisci descrizione."
                    }


                }

            },
            shape = RoundedCornerShape(5.dp),
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .align(Alignment.CenterHorizontally),
            enabled = description.isNotBlank() && location != null
        ) {
            Text("Crea Post")
        }

        Text(
            text = resultText,
            color = Color.White,
            modifier = Modifier.padding(16.dp)
        )
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
            .clickable { navController.navigate("mapPage/${title}")},
        actions = {
            IconButton(onClick = {
                navController.navigate("addPost/${title}")
            }) {
                Icon(
                    //Dovrebbe Funzionare
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

//
// 10 (Comment Page)
//
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentPage(navController: NavHostController, postId: Int) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current.applicationContext

    var post by remember { mutableStateOf<Post?>(null) }
    var author by remember { mutableStateOf("") }
    var comments by remember { mutableStateOf<List<Comment>>(emptyList()) }
    var newComment by remember { mutableStateOf("") }

    LaunchedEffect(postId) {
        val api = RetrofitInstance.api
        val fetchedPost = getPostById(api, postId)
        post = fetchedPost
        author = post?.let { getUsernameById(api, it.author) } ?: "Unknown"
        comments = getCommentsByPostId(api, postId)
    }

    post?.let { currentPost ->
        val text = currentPost.text
        val imageLink = post!!.image?.replaceFirst("http://", "https://") ?: ""
        val lat = currentPost.location_lat ?: 0.0
        val lng = currentPost.location_lng ?: 0.0

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(79, 149, 157))
                .verticalScroll(rememberScrollState())
        ) {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )

            // Post Info
            Text("Autore: $author", color = Color.White, modifier = Modifier.padding(8.dp))
            Text("Testo: $text", color = Color.White, modifier = Modifier.padding(8.dp))
            AsyncImage(
                model = imageLink,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Fit
            )
            Text("Posizione: ($lat, $lng)", color = Color.White, modifier = Modifier.padding(8.dp))

            Divider(color = Color.White, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))

            // Comments Section
            Text("Commenti:", color = Color.White, fontSize = 20.sp, modifier = Modifier.padding(start = 8.dp))

            comments.forEach { comment ->
                CommentElement(navController, comment.text.toString(), comment.author)
            }

            // Add new comment
            OutlinedTextField(
                value = newComment,
                onValueChange = { newComment = it },
                label = { Text("Scrivi un commento") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )

            Button(
                onClick = {
                    coroutineScope.launch {
                        val api = RetrofitInstance.api
                        val userId = SecureStorage.getUser(context)
                        val result = addCommentToPost(api, postId, userId, newComment)

                        if (result != "") {
                            comments = getCommentsByPostId(api, postId)
                            newComment = ""
                        }
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 16.dp),
                enabled = newComment.isNotBlank()
            ) {
                Text("Invia Commento")
            }
        }
    } ?: run {
        // Loading state
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Caricamento post...", color = Color.White)
        }
    }
}

//Riga Di commento Con Autore e Testo
@Composable
fun CommentElement(navController: NavHostController, text: String, author: Int) {
    var authorUsername by remember { mutableStateOf("") }
    LaunchedEffect(author) {
        val api = RetrofitInstance.api
        authorUsername = getUsernameById(api, author).toString()
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = authorUsername,
            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold),
            modifier = Modifier.padding(bottom = 2.dp)
        )
        Text(
            text = text,
            style = TextStyle(fontSize = 16.sp),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Divider(color = Color.Gray, thickness = 1.dp)
    }
}

//
// (GamePage)
//
@Composable
fun GamePage(navController: NavHostController) {
    Scaffold(
        topBar = { HeaderBar(navController, "Happy Green") },
        bottomBar = { BottomNavBar(navController) }
    ) { paddingValues ->

        // Wrappa il contenuto con un layout e applica il padding dello scaffold
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(onClick = { navController.navigate("quiz") }) {
                Text("Inizia Quiz")
            }
        }
    }
}

@Composable
fun QuizPage(navController: NavHostController, questions: List<QuizQuestion>) {
    var currentIndex by remember { mutableStateOf(0) }
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var showNextButton by remember { mutableStateOf(false) }
    var shuffledAnswers by remember { mutableStateOf(listOf<String>()) }
    var score by remember { mutableStateOf(0) }

    if (questions.isNotEmpty()) {
        val currentQuestion = questions.getOrNull(currentIndex)

        LaunchedEffect(currentQuestion?.question_text) {
            currentQuestion?.let {
                selectedAnswer = null
                showNextButton = false
                shuffledAnswers = (it.wrong_answers + it.correct_answer).shuffled()
            }
        }

        currentQuestion?.let { question ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Domanda ${currentIndex + 1}/${questions.size}",
                    style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = question.question_text,
                    style = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                shuffledAnswers.forEach { answer ->
                    val isSelected = selectedAnswer == answer
                    val backgroundColor = when {
                        !showNextButton -> Color.LightGray
                        answer == question.correct_answer -> Color(0xFFAAF683)
                        isSelected && answer != question.correct_answer -> Color(0xFFFF686B)
                        else -> Color.LightGray
                    }

                    Button(
                        onClick = {
                            if (selectedAnswer == null) {
                                selectedAnswer = answer
                                showNextButton = true
                                if (answer == question.correct_answer) {
                                    score++
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
                        enabled = selectedAnswer == null
                    ) {
                        Text(answer)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (showNextButton) {
                    Button(
                        onClick = {
                            if (currentIndex < questions.lastIndex) {
                                currentIndex++
                            } else {
                                navController.navigate("resultpage/$score")
                            }
                        }
                    ) {
                        Text(if (currentIndex < questions.lastIndex) "Next" else "Finish")
                    }
                }
            }
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Caricamento domande...")
        }
    }
}

@Composable
fun QuizResultPage(score: Int, navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Hai totalizzato $score su 5!", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = { navController.navigate("game") }) {
            Text("Torna Schemata Giochi")
        }
    }
}




//TODO
@Composable
fun CameraPage(navController: NavHostController) {
    Scaffold(
        topBar = { HeaderBar(navController, "Happy Green") },
        bottomBar = { BottomNavBar(navController) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Button(onClick = {navController.navigate("quizpage")}) { Text("Ciaone") }
        }
    }
}

//TODO
@Composable
fun UserPage(navController: NavHostController) {
//    val context = LocalContext.current.applicationContext
//    val coroutineScope = rememberCoroutineScope()
//    val id = SecureStorage.getUser(context)
//    var utente by remember { mutableStateOf<User?>(null) }
//
//    LaunchedEffect(id) {
//        val api = RetrofitInstance.api
//        utente = api.getUser(id)
//    }
//
//    utente?.let { currentUser ->
//        val usrn = currentUser.username
//        val pic = utente!!.profile_pic?.replaceFirst("http://", "https://") ?: "https://stock.adobe.com/search/images?k=default+user"
//        val pts = currentUser.points ?: 0
//        val lvl = currentUser.level ?: 0
//        val dte = currentUser.date_joined ?: null
//        var resultText by remember { mutableStateOf("") }
//        var imageUri by remember { mutableStateOf<Uri?>(null) }
//        val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
//            imageUri = uri
//        }
//        Scaffold(
//            topBar = { HeaderBar(navController, "Happy Green") },
//            bottomBar = { BottomNavBar(navController) }
//        ) { paddingValues ->
//            Column(
//                modifier = Modifier
//                    .padding(paddingValues)
//                    .verticalScroll(rememberScrollState())
//                    .fillMaxSize()
//                    .padding(16.dp),
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.Top
//            ) {
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(8.dp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Image(
//                        painter = rememberAsyncImagePainter(
//                            pic
//                        ),
//                        contentDescription = "Profile picture",
//                        modifier = Modifier
//                            .size(100.dp)
//                            .clip(CircleShape)
//                            .border(2.dp, Color.Gray, CircleShape)
//                    )
//
//                    Spacer(modifier = Modifier.width(16.dp))
//
//                    // Dati utente
//                    Column {
//                        Text("Username: $usrn.", fontWeight = FontWeight.Bold)
//                        Text("Punti: $pts")
//                        Text("Livello: $lvl")
//                        Text(
//                            "Registrato il: ${
//                                (dte as String).substring(
//                                    0,
//                                    10
//                                )
//                            }"
//                        )
//                    }
//                }
//
//                Spacer(modifier = Modifier.height(24.dp))
//
//                Button(
//                    onClick = {
//                        SecureStorage.clearToken(context)
//                        navController.navigate("first")
//                    },
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    Text("Logout")
//                }
//
//                Spacer(modifier = Modifier.height(12.dp))
//
//                Button(
//                    onClick = {
//                        launcher.launch("image/*")
//                        coroutineScope.launch {
//                            val api = RetrofitInstance.api
//
//                            if (imageUri != null ) {
//                                val userData = UserData(
//                                    Id = id,
//                                    username = usrn,
//                                    imageUri = imageUri!!,
//                                    points = pts,
//                                    level = lvl
//                                )
//
//                                resultText = updateUser(api, context, userData)
//
//
//                            } else {
//                                resultText = "Seleziona immagine, posizione e inserisci descrizione."
//                            }
//
//
//                        }
//                    },
//                    modifier = Modifier.align(Alignment.CenterHorizontally)
//                ) {
//                    Text("Modifica Immagine Profilo")
//                }
//            }
//        }
//    }
}

//TODO
@Composable
fun UploadPicture(navController: NavHostController){

}

//TODO
@Composable
fun OptionsPage(navController: NavHostController) {
    Scaffold(
        topBar = { HeaderBar(navController, "Happy Green") },
        bottomBar = { BottomNavBar(navController) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.maxwell), // your drawable
                contentDescription = "Centered Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.78f), // adjust ratio to your image aspect (e.g., 16:9 ~ 1.78)
                contentScale = ContentScale.FillWidth
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



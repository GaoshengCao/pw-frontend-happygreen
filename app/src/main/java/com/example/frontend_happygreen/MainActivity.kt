package com.example.frontend_happygreen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.frontend_happygreen.ui.theme.FrontendhappygreenTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FrontendhappygreenTheme {
                val navController = rememberNavController()
                Surface (modifier = Modifier.fillMaxSize())
                {
                    NavHost(navController = navController, startDestination = "main") {
                        composable("main") { HomePage(navController) }
                    }
                }
            }
        }
    }
}

@Composable
fun FirstPage(navController: NavHostController) {

    //Nome App

    //2 Bottoni
    // LOGIN
    // Registrazione

}

@Composable
fun LoginPage(navController: NavHostController) {

    //Nome App

    //TextBox UserName
    //TextBox Password
    //Button Invia
}

@Composable
fun RegisterPage(navController: NavHostController) {

    //Nome App

    //TextBox UserName
    //TextBox Password
    //Button Invia

}

@Composable
fun HomePage(navController: NavHostController) {
    Box(modifier = Modifier.fillMaxSize().background(Color.White)){
        HeaderBar(navController)
        //Fa richiesta della lista di gruppi relativo all'utente

        //Se utente non fa parte di nessun gruppo

        val mypainter = painterResource(R.drawable.menu)
        Image(painter = mypainter,    contentDescription = "Maxwell Image",
            modifier = Modifier.fillMaxSize())
        BottomNavBar(navController)
    }

    //Barra Sopra Nome App

    //Contenuto: Lista Gruppi di Appartenenza
    // Bottone Creazione Gruppo , unione

    //Botton Bar

}


@Composable
fun CreateGroupPage(navController: NavHostController) {

    //TextBox Nome Gruppo
    //Bottone Invia


}

@Composable
fun EnterGroupPage(navController: NavHostController) {

    //TextBox Codice Gruppo
    //Bottone Invia

}

@Composable
fun GroupPage(navController: NavHostController) {

    //Barra sopra Nome Gruppo

    //Contenuto Tutti i Post

    //Botton Bar
}

@Composable
fun AddPostPage(navController: NavHostController) {

    //TextBox Descrizione Post
    //Carica Immagine
    //Inserimento Luogo

}

@Composable
fun CommentPage(navController: NavHostController) {

    //Lista Commenti:
    //Nome Utente
    //Commento

}

@Composable
fun GroupMapPage(navController: NavHostController) {

    //Mappa con Tutte le Punte Di dove Sono I post
}

@Composable
fun BottomNavBar(navController: NavHostController) {

    //MiniGiochi
    //Camera Scan
    //Home
    //Pagina Utente
    //Impostazioni

}

@Composable
fun HeaderBar(navController: NavHostController) {

}


//TODO
//Controllo Sessione se utente è Acceduto
//Controllo su utente fa parte di gruppi
//
//Pagine della NavBar

@Composable
fun GreetingPreview() {

}
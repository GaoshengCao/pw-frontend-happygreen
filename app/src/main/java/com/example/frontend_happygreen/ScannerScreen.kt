package com.example.frontend_happygreen

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

@OptIn(ExperimentalGetImage::class)
@Composable
fun ScannerScreen(modifier: Modifier = Modifier) {
    // State variables
    var hasCameraPermission by remember { mutableStateOf(false) }
    var detectionResult by remember { mutableStateOf("Nessun oggetto rilevato") }
    var isProcessing by remember { mutableStateOf(false) }
    var isScanning by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    var imageAnalysis by remember { mutableStateOf<ImageAnalysis?>(null) }

    // Color scheme for elegant UI
    val gradientBackground = Brush.verticalGradient(
        colors = listOf(Color(0xFF4CAF50), Color(0xFF81C784))
    )
    val cardBackground = Color.White
    val accentColor = Color(0xFF388E3C)
    val textColor = Color(0xFF212121)

    // Waste category colors
    val umidoColor = Color(0xFFE8F5E9)
    val cartaColor = Color(0xFFE3F2FD)
    val plasticaColor = Color(0xFFFFF9C4)
    val vetroColor = Color(0xFFB2EBF2)
    val pianteColor = Color(0xFFDCEDC8)
    val defaultColor = Color(0xFFF5F5F5)

    // Scroll state
    val scrollState = rememberScrollState()

    // Camera permission launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    // Check permission on start
    LaunchedEffect(Unit) {
        hasCameraPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        if (!hasCameraPermission) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Main UI layout
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBackground)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState), // Enable scrolling
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Title
            Text(
                text = "HappyGreen Scanner",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    color = Color.White
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )



            if (hasCameraPermission) {
                // Camera preview in a card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                        .shadow(8.dp, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .border(2.dp, accentColor, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = cardBackground)
                ) {
                    AndroidView(
                        factory = { PreviewView(it) },
                        modifier = Modifier.fillMaxSize()
                    ) { previewView ->
                        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                        cameraProviderFuture.addListener({
                            val cameraProvider = cameraProviderFuture.get()
                            val preview = Preview.Builder().build().also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }

                            val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

                            imageAnalysis = ImageAnalysis.Builder()
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .build()

                            imageAnalysis?.setAnalyzer(cameraExecutor) { imageProxy ->
                                if (isScanning) {
                                    isProcessing = true
                                    val mediaImage = imageProxy.image
                                    if (mediaImage != null) {
                                        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

                                        labeler.process(image)
                                            .addOnSuccessListener { labels ->
                                                coroutineScope.launch(Dispatchers.Main) {
                                                    val texts = labels.map { it.text }

                                                    val umido = listOf(
                                                        "Banana", "Apple", "Orange", "Fruit", "Vegetable", "Lettuce", "Tomato", "Cucumber",
                                                        "Pizza", "Bread", "Cake", "Pie", "Pasta", "Meat", "Fish", "Egg", "Cheese", "Couscous",
                                                        "Leftovers", "Food", "Lunch", "Dinner", "Meal", "Peel", "Shell", "Organic waste",
                                                        "Avocado", "Onion", "Garlic", "Carrot", "Zucchini", "Spinach", "Salad", "Soup", "Rice",
                                                        "Coffee grounds", "Tea bag", "Biowaste"
                                                    )

                                                    val plastica = listOf(
                                                        "Bottle", "Plastic bottle", "PET", "Plastic", "Packaging", "Straw", "Cutlery", "Fork",
                                                        "Spoon", "Knife", "Tupperware", "Food container", "Plastic bag", "Bag", "Wrapper", "Toothbrush",
                                                        "Shampoo", "Soap container", "Detergent", "Lid", "Cap", "Lego", "Toy", "Glasses", "Sunglasses",
                                                        "Bracelet", "CD", "DVD", "Credit card", "Plastic cup", "Umbrella", "Mat", "Pen", "Highlighter",
                                                        "Remote", "Mouse", "Charger", "Cable", "Headphones", "Plastic wrap", "Cling film", "Polystyrene"
                                                    )

                                                    val carta = listOf(
                                                        "Paper", "Newspaper", "Notebook", "Book", "Magazine", "Tissue", "Napkin", "Menu",
                                                        "Screenshot", "Document", "Pass", "Envelope", "Cardboard", "Carton", "Toilet paper",
                                                        "Letter", "Flyer", "Poster", "Instructions", "Manual", "Ticket", "Receipt", "Bill",
                                                        "Notepad", "Calendar", "Brochure", "Sketch", "Origami", "Writing", "Diary", "Agenda",
                                                        "Comic", "Story", "Paper bag", "Packing paper", "Recycled paper"
                                                    )

                                                    val vetro = listOf(
                                                        "Glass", "Glass bottle", "Wine", "Wine bottle", "Beer bottle", "Jar", "Vase", "Cup", "Glass cup",
                                                        "Perfume bottle", "Cologne", "Glass container", "Broken glass", "Shot glass", "Mason jar",
                                                        "Olive oil bottle", "Sauce bottle", "Jam jar", "Pickle jar", "Juice bottle", "Syrup bottle",
                                                        "Liquor bottle"
                                                    )

                                                    val piante = listOf(
                                                        "Plant", "Flower", "Tree", "Leaf", "Bush", "Shrub", "Branch", "Root", "Herb", "Grass",
                                                        "Pinecone", "Seeds", "Cactus", "Palm", "Fern", "Bamboo", "Ivy", "Moss", "Petals", "Stems",
                                                        "Leaves", "Trunk", "Sapling", "Soil with plant", "Compostable plant"
                                                    )

                                                    // Dizionario delle descrizioni ambientali
                                                    val impactDescriptions = mapOf(
                                                        //plastica
                                                        "Plastic" to "La plastica impiega secoli per degradarsi. Riciclare aiuta a ridurre l’inquinamento da microplastiche.",
                                                        "Plastic bag" to "I sacchetti di plastica sono pericolosi per la fauna marina. Usa alternative riutilizzabili.",
                                                        "PET" to "Il PET è un tipo di plastica riciclabile. Va conferito nella raccolta della plastica.",
                                                        "Polystyrene" to "Il polistirolo è difficile da riciclare e spesso finisce nelle discariche.",
                                                        "Plastic cup" to "I bicchieri di plastica monouso sono una delle principali fonti di rifiuti nei parchi e spiagge.",
                                                        "Shampoo" to "I flaconi per shampoo vanno svuotati e gettati nella plastica.",
                                                        "Food container" to "I contenitori alimentari in plastica sono riciclabili solo se puliti dai residui.",
                                                        "Wrapper" to "Le confezioni miste (plastica/alluminio) sono difficili da riciclare. Meglio evitarle.",
                                                        //umido
                                                        "Fruit" to "I residui di frutta possono essere compostati, contribuendo a ridurre i rifiuti organici.",
                                                        "Vegetable" to "Gli scarti vegetali sono ideali per il compost domestico o il bidone dell'umido.",
                                                        "Banana" to "La buccia di banana si decompone facilmente ed è ottima per il compost.",
                                                        "Tomato" to "Gli scarti di pomodoro vanno sempre nell'umido. Aiutano a generare fertilizzante.",
                                                        "Egg" to "I gusci d’uovo sono biodegradabili. Vanno nell’umido o nel compost.",
                                                        "Cheese" to "I resti di formaggio, se non confezionati, vanno nell’umido.",
                                                        "Coffee grounds" to "I fondi di caffè sono compostabili e arricchiscono il terreno.",
                                                        "Tea bag" to "Le bustine di tè (senza punto metallico) possono essere compostate.",
                                                        //carta
                                                        "Notebook" to "I quaderni senza spirale metallica possono essere riciclati con la carta.",
                                                        "Magazine" to "Le riviste patinate sono riciclabili, ma non se plastificate.",
                                                        "Cardboard" to "Il cartone va appiattito e conferito nella raccolta carta.",
                                                        "Tissue" to "I fazzoletti usati vanno nell’umido se compostabili, altrimenti nell’indifferenziato.",
                                                        "Flyer" to "I volantini pubblicitari sono spesso riciclabili, ma evitali se inutili.",
                                                        "Envelope" to "Le buste vanno nella carta se non contengono plastica.",
                                                        "Receipt" to "Gli scontrini termici vanno nell’indifferenziato: contengono BPA.",

                                                        //vetro
                                                        "Glass" to "Il vetro può essere riciclato infinite volte senza perdere qualità.",
                                                        "Perfume bottle" to "I flaconi di profumo vanno nel vetro se vuoti e privi di tappi in plastica o metallo.",
                                                        "Jar" to "I barattoli in vetro puliti vanno nel contenitore del vetro.",
                                                        "Wine bottle" to "Le bottiglie di vino sono vetro riciclabile. Rimuovi tappi e capsule prima di buttarle.",
                                                        "Glass container" to "Contenitori in vetro possono essere riutilizzati o conferiti nel vetro.",

                                                        //piante
                                                        "Flower" to "I fiori appassiti possono essere smaltiti nel bidone del verde o compostati.",
                                                        "Leaves" to "Foglie secche e rami piccoli sono adatti al compostaggio domestico.",
                                                        "Root" to "Le radici e gli scarti di potatura devono essere smaltiti nel verde.",
                                                        "Grass" to "L’erba tagliata va raccolta e conferita nel bidone del verde.",
                                                        "Bush" to "I cespugli potati vanno smaltiti come verde. Non abbandonarli nell’ambiente.",

                                                        //altro
                                                        "Toothbrush" to "Gli spazzolini in plastica non sono riciclabili. Meglio preferire quelli in bambù.",
                                                        "Pen" to "Le penne a sfera non si riciclano. Riduci l’uso e cerca alternative ricaricabili.",
                                                        "Remote" to "I telecomandi sono RAEE. Vanno portati in un centro di raccolta elettronica.",
                                                        "Charger" to "I caricabatterie contengono componenti elettronici. Non gettarli nell’indifferenziato.",
                                                        "CD" to "I CD e DVD sono rifiuti elettronici. Vanno smaltiti nei centri RAEE."
                                                    )

                                                    val matchedLabel = texts.firstOrNull { it in umido + plastica + carta + vetro + piante }
                                                    val impactDescriptionText = impactDescriptions[matchedLabel] ?: "Ricorda sempre di smaltirlo correttamente per proteggere l’ambiente."

                                                    detectionResult = when {
                                                        texts.any { it in umido } -> "Oggetti riconosciuti: $texts\nDa buttare in: Umido\n\n$impactDescriptionText"
                                                        texts.any { it in plastica } -> "Oggetti riconosciuti: $texts\nDa buttare in: Plastica\n\n$impactDescriptionText"
                                                        texts.any { it in carta } -> "Oggetti riconosciuti: $texts\nDa buttare in: Carta\n\n$impactDescriptionText"
                                                        texts.any { it in vetro } -> "Oggetti riconosciuti: $texts\nDa buttare in: Vetro\n\n$impactDescriptionText"
                                                        texts.any { it in piante } -> "Oggetti riconosciuti: $texts\nDa buttare in: Bidone del verde\n\n$impactDescriptionText"
                                                        else -> "Oggetti riconosciuti: $texts\nNon adatto al riciclaggio"
                                                    }

                                                    isScanning = false
                                                    isProcessing = false
                                                }
                                            }
                                            .addOnFailureListener {
                                                coroutineScope.launch(Dispatchers.Main) {
                                                    detectionResult = "Errore nell'analisi"
                                                    isProcessing = false
                                                }
                                            }
                                            .addOnCompleteListener {
                                                imageProxy.close()
                                            }
                                    } else {
                                        imageProxy.close()
                                    }
                                } else {
                                    imageProxy.close()
                                }
                            }

                            try {
                                cameraProvider.unbindAll()
                                cameraProvider.bindToLifecycle(
                                    lifecycleOwner,
                                    CameraSelector.DEFAULT_BACK_CAMERA,
                                    preview,
                                    imageAnalysis
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }, ContextCompat.getMainExecutor(context))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Scan Again button
                if (!isScanning && detectionResult != "Nessun oggetto rilevato") {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            isScanning = true
                            detectionResult = "Nessun oggetto rilevato"
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = accentColor,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Scansiona di nuovo",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                // Result display
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .shadow(4.dp, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            detectionResult.contains("Umido") -> umidoColor
                            detectionResult.contains("Plastica") -> plasticaColor
                            detectionResult.contains("Carta") -> cartaColor
                            detectionResult.contains("Vetro") -> vetroColor
                            detectionResult.contains("Bidone delle piante") -> pianteColor
                            else -> defaultColor
                        }
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isProcessing) Icons.Default.Refresh else Icons.Default.Info,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = detectionResult,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 18.sp,
                                color = textColor
                            )
                        )
                    }
                }


            } else {
                // Permission denied UI
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .shadow(4.dp, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Camera,
                            contentDescription = null,
                            tint = Color(0xFFE64A19),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Permesso della telecamera necessario",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 18.sp,
                                color = textColor
                            ),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = accentColor,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Richiedi Permesso")
                        }
                    }
                }
            }

            // Additional space for scrolling
            Spacer(modifier = Modifier.height(50.dp))
        }
    }

    // Cleanup executor on composable disposal
    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }
}
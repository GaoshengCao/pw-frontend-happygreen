# HappyGreen Frontend App

<div align="center">
  <img src=app/src/main/res/drawable/logo.png alt="HappyGreen Logo" width="150">
  <h3>Sostenibilità a portata di click</h3>
</div>

## 📋 Indice
- [Descrizione del progetto](#-descrizione-del-progetto)
- [Funzionalità](#-funzionalità)
- [Requisiti di sistema](#-requisiti-di-sistema)
- [Installazione](#-installazione)
  - [Computer](#computer)
  - [Telefono](#telefono)
- [Struttura del progetto](#-struttura-del-progetto)
- [Tecnologie utilizzate](#-tecnologie-utilizzate)
- [Team di sviluppo](#-team-di-sviluppo)
- [Licenza](#-licenza)
- [NOTA BENE](#-nota-bene)

## 📝 Descrizione del Progetto

HappyGreen è un'applicazione mobile Android progettata per sensibilizzare gli utenti sul tema della sostenibilità ambientale attraverso il riconoscimento di oggetti, quiz educativi e condivisione di informazioni ecologiche. Questo repository contiene il backend dell'applicazione, sviluppato con Django REST Framework.

Il progetto è stato realizzato come parte del corso TPSIT (Tecnologie e Progettazione di Sistemi Informatici e di Telecomunicazioni) per offrire agli studenti uno strumento divertente e interattivo per imparare a prendersi cura dell'ambiente.

## 🔍 Funzionalità

Il frontend supporta le seguenti funzionalità:

- 🔒 **Autenticazione**: Registrazione e autenticazione tramite JWT
- 📷 **Scanner ambientale**: Uso di machine learning per capire l'ambiente circostante
- 📱 **Condivisione Contenuti**: Post e commenti per condividere esperienze 
- 🏆 **Quiz e giochi educativi**: Quiz educativi sulla sostenibilità con sistema di punteggio
- 👥 **Creazione e partecipazione a gruppi**: Interazione sociale tra amici grazie ai gruppi
- 📍 **Post con geolocalizzazione e immagini**: Possibilità di vedere dove sono gli amici
- 🎮 **Profilo utente con punti e livello**: Punti e livelli per alimentare l'interazione con l'app

## 💻 Requisiti di Sistema

- Android Studio
- Dispositivo / Emulatore Android (API 24+)
- Connessione ad internet
- Amici (facoltativo)

## 🚀 Installazione

### Computer

1. Apri Android Studio

2. Seleziona "clona un repository"

3. Inserisci il link del codice
   https://github.com/GaoshengCao/pw-frontend-happygreen.git

4.Cambiare in API.kt dell Object RetrofitInstance BASE_URL con l'url del server su è eseguito il serve con pw-backend-happygreen
   https://github.com/GaoshengCao/pw-backend-happygreen
   
5. Esegui il programma
   Collega tramite cavo il telefono o scarica un emulatore Android.

### Telefono

1. Scarica il file ```.apk ```

2. Clicca sul file per installarlo

3. Apri l'applicazione.

## 📁 Struttura del Progetto

- ``` SplashScreen ```      → schermata di avvio con animazione
- ``` FirstPage ```         → scelta tra login o registrazione
- ``` LoginPage ```         → autenticazione utente
- ``` RegisterPage ```      → registrazione nuovo utente
- ``` HomePage ```          → elenco gruppi e navigazione
- ``` CreateGroupPage ```   → creazione nuovi gruppi
- ``` JoinGroupPage ```     → partecipazione a gruppi esistenti
- ``` GroupPage ```         → visualizzazione post del gruppo
- ``` AddPostPage ```       → aggiunta post con immagine e posizione
- ``` CommentPage ```       → sezione commenti ad un post
- ``` GamePage ```          → schermata iniziale dei giochi
- ``` QuizPage ```          → quiz educativi
- ``` QuizResultPage ```    → risultati dei quiz
- ``` CameraPage ```        → scanner oggetti eco (in lavorazione)
- ``` UserPage ```          → profilo utente (in sviluppo)
- ``` OptionsPage ```       → pagina extra o placeholder

## 🛠 Tecnologie Utilizzate

- **App**: Android Studio
- **API**: Retrofit con API backend-happygreen e google maps
- **Machine Learning**: ML Kit
- **Maxwell**: Maxwell

## 👨‍💻 Team di Sviluppo

Progetto realizzato da:
- Edoardo Cortivo
- Cao Gaosheng
- Veltroni Dario

## 📄 Licenza

Questo progetto **non ha una licenza**.
Tutto il codice è coperto dai diritti di autore.
Questo progetto **non** è open-source 

## ⚠️ Nota Bene

L'applicazione, a causa di inesperienza nella progettazione di sistemi full-stack, presenta alcuni errori e bug.
-Il punteggio relativo all'utente non può cambia, Causa nell' API il punteggio è Read-Only. 
-Il livello relativo all'utente non può cambia, Causa nell' API il livello è Read-Only.

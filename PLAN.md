# Udvash Student Client - Project Plan

## Overview
A native Android application for Udvash students to access class notes, practice notes, and past classes. The app will be built using Kotlin and Jetpack Compose, following Material Design 3 principles. It will act as a client for the `online.udvash-unmesh.com` portal.

## User Credentials
*   **Registry Number:** 4019749
*   **Password:** (Stored securely, used for login flow)

## Tech Stack
*   **Language:** Kotlin
*   **UI Framework:** Jetpack Compose (Material 3)
*   **Architecture:** MVVM (Model-View-ViewModel)
*   **Networking:** Retrofit + OkHttp (with `CookieJar` for session management)
*   **HTML Parsing:** Jsoup (to scrape data from the website as there is no public JSON API)
*   **Async/Concurrency:** Kotlin Coroutines & Flow
*   **Dependency Injection:** Manual (initially) or Hilt
*   **Navigation:** Jetpack Navigation Compose
*   **Image Loading:** Coil
*   **Video Player:** Media3 (ExoPlayer)
*   **PDF Viewer:** AndroidPdfViewer or Google PDF Viewer Intent

## App Structure
```
com.udvash.student
├── data
│   ├── model          # Data classes (Student, Class, Exam)
│   ├── remote         # Retrofit services, HTML parsers
│   └── repository     # Data repositories (AuthRepository, ClassRepository)
├── domain             # Use cases (optional for simple app)
├── ui
│   ├── theme          # Material 3 Theme definitions
│   ├── components     # Reusable UI components
│   ├── screens
│   │   ├── login      # LoginScreen & LoginViewModel
│   │   ├── dashboard  # DashboardScreen (Grid of options)
│   │   ├── classes    # ClassListScreen (Past Classes)
│   │   ├── player     # VideoPlayerScreen
│   │   └── pdf        # PdfViewerScreen
│   └── navigation     # NavGraph
└── utils              # Extensions, Constants
```

## Feature Implementation Plan

### 1. Authentication (Login)
*   **Flow:**
    1.  User enters Registration Number.
    2.  App POSTs to `/Account/Password` with `RegistrationNumber` & scraped `__RequestVerificationToken`.
    3.  User enters Password.
    4.  App POSTs to `/Account/Login` with `Password` and cookies from step 2.
    5.  On success (redirect to Dashboard), save cookies persistently.

### 2. Dashboard
*   **Endpoint:** `/Dashboard`
*   **Data to Scrape:**
    *   Student Name & ID
    *   Menu Links (Live Class, Past Classes, Solve Sheet, etc.)
*   **UI:** A modern grid layout with icons for each section.

### 3. Past Classes (Core Feature)
*   **Endpoint:** `/Routine/PastClasses` (Likely contains a list of classes)
*   **Data to Scrape:**
    *   Class Title / Subject
    *   Date / Time
    *   Video URL (Vimeo/YouTube embed link)
    *   Lecture Note URL (PDF)
    *   Practice Note URL (PDF)
*   **UI:** LazyColumn displaying classes. Clicking an item expands details or navigates to a details screen.

### 4. Class Playback & Notes
*   **Video:** Embed ExoPlayer to play the class video.
*   **Notes:** Buttons to "View Class Note" and "View Practice Note". These will either open in an in-app PDF viewer or download and open via system intent.

## Development Phases
1.  **Setup:** Initialize Android project, configure Gradle, set up Git repo.
2.  **Network Layer:** Build the `NetworkClient` with Cookie persistence and `Jsoup` parsers.
3.  **Auth:** Implement the 2-step login flow.
4.  **Dashboard:** Create the main landing screen.
5.  **Classes:** Implement the scraping and display of past classes.
6.  **Details:** Add video playback and PDF viewing.
7.  **Polish:** Material 3 theming, dark mode support, error handling.

## GitHub Repository
*   **Remote:** `https://github.com/salmanbappi/udvash-student-client` (To be created)
*   **Branching:** `main` for stable, feature branches for development.

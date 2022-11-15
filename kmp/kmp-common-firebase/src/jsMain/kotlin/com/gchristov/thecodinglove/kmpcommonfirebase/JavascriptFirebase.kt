package com.gchristov.thecodinglove.kmpcommonfirebase

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseApp
import dev.gitlive.firebase.FirebaseOptions
import dev.gitlive.firebase.initialize

actual fun provideFirebaseApp(): FirebaseApp = requireNotNull(Firebase.initialize(options = FirebaseOptions(
    apiKey = BuildKonfig.FIREBASE_API_KEY,
    authDomain = BuildKonfig.FIREBASE_AUTH_DOMAIN,
    projectId = BuildKonfig.FIREBASE_PROJECT_ID,
    storageBucket = BuildKonfig.FIREBASE_STORAGE_BUCKET,
    gcmSenderId = BuildKonfig.FIREBASE_GCM_SENDER_ID,
    applicationId = BuildKonfig.FIREBASE_APPLICATION_ID
)))
package com.gchristov.thecodinglove.kmpcommonfirebase

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseApp
import dev.gitlive.firebase.FirebaseOptions
import dev.gitlive.firebase.initialize

actual fun provideFirebaseApp(
    options: FirebaseOptions
): FirebaseApp = requireNotNull(Firebase.initialize(options = options))
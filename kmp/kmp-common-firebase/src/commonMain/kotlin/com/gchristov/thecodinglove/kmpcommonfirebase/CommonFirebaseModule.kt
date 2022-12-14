package com.gchristov.thecodinglove.kmpcommonfirebase

import com.gchristov.thecodinglove.kmpcommondi.DiModule
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseApp
import dev.gitlive.firebase.FirebaseOptions
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.firestore
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

object CommonFirebaseModule : DiModule() {
    override fun name() = "kmp-common-firebase"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindSingleton { provideFirebaseOptions() }
            bindSingleton { provideFirebaseApp(options = instance()) }
            bindSingleton { provideFirestore(app = instance()) }
        }
    }

    private fun provideFirebaseOptions(): FirebaseOptions = FirebaseOptions(
        apiKey = BuildKonfig.FIREBASE_API_KEY,
        authDomain = BuildKonfig.FIREBASE_AUTH_DOMAIN,
        projectId = BuildKonfig.FIREBASE_PROJECT_ID,
        storageBucket = BuildKonfig.FIREBASE_STORAGE_BUCKET,
        gcmSenderId = BuildKonfig.FIREBASE_GCM_SENDER_ID,
        applicationId = BuildKonfig.FIREBASE_APPLICATION_ID
    )

    private fun provideFirestore(app: FirebaseApp): FirebaseFirestore = Firebase.firestore(app)
}

internal expect fun provideFirebaseApp(options: FirebaseOptions): FirebaseApp
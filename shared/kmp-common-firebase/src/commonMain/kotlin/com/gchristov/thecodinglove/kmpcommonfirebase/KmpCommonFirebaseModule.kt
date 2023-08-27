package com.gchristov.thecodinglove.kmpcommonfirebase

import com.gchristov.thecodinglove.kmpcommonkotlin.BuildConfig
import com.gchristov.thecodinglove.kmpcommonkotlin.di.DiModule
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseApp
import dev.gitlive.firebase.FirebaseOptions
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.firestore
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

object KmpCommonFirebaseModule : DiModule() {
    override fun name() = "kmp-common-firebase"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindSingleton { provideFirebaseOptions() }
            bindSingleton { provideFirebaseApp(options = instance()) }
            bindSingleton { provideFirestore(app = instance()) }
        }
    }

    private fun provideFirebaseOptions(): FirebaseOptions = FirebaseOptions(
        applicationId = BuildConfig.GCP_FIREBASE_APPLICATION_ID,
        apiKey = BuildConfig.GCP_FIREBASE_API_KEY,
        projectId = BuildConfig.GCP_PROJECT_ID,
    )

    private fun provideFirestore(app: FirebaseApp): FirebaseFirestore = Firebase.firestore(app)
}

internal expect fun provideFirebaseApp(options: FirebaseOptions): FirebaseApp
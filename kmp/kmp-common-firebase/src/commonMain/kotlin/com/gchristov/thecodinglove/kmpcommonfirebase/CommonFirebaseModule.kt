package com.gchristov.thecodinglove.kmpcommonfirebase

import com.gchristov.thecodinglove.kmpcommondi.DiModule
import com.gchristov.thecodinglove.kmpcommondi.inject
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseApp
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.firestore
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

object CommonFirebaseModule : DiModule() {
    override fun name() = "kmp-common-firebase"

    override fun bindLocalDependencies(builder: DI.Builder) {
        builder.apply {
            bindSingleton { provideFirebaseApp() }
            bindSingleton { provideFirestore(app = instance()) }
        }
    }

    fun injectFirestore(): FirebaseFirestore = inject()

    private fun provideFirestore(app: FirebaseApp): FirebaseFirestore = Firebase.firestore(app)
}

internal expect fun provideFirebaseApp(): FirebaseApp
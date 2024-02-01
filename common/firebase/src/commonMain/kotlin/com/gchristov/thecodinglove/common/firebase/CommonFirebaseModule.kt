package com.gchristov.thecodinglove.common.firebase

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.firebase.firestore.FirestoreMigration
import com.gchristov.thecodinglove.common.firebase.firestore.ScratchMigration
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.kotlin.di.DiModule
import kotlinx.coroutines.Dispatchers
import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.bindSingleton
import org.kodein.di.instance

object CommonFirebaseModule : DiModule() {
    override fun name() = "common-firebase"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindSingleton { provideFirebaseAdmin() }
            bindProvider {
                provideFirestoreMigrations(
                    log = instance(),
                    firebaseAdmin = instance(),
                    jsonSerializer = instance(),
                )
            }
        }
    }

    private fun provideFirestoreMigrations(
        log: Logger,
        firebaseAdmin: FirebaseAdmin,
        jsonSerializer: JsonSerializer.ExplicitNulls,
    ): List<FirestoreMigration> = listOf(
        ScratchMigration(
            dispatcher = Dispatchers.Default,
            log = log,
            firebaseAdmin = firebaseAdmin,
            jsonSerializer = jsonSerializer,
        )
    )
}

expect fun provideFirebaseAdmin(): FirebaseAdmin
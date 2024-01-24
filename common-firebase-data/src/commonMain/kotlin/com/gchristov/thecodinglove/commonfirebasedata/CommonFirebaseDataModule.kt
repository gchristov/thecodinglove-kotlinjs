package com.gchristov.thecodinglove.commonfirebasedata

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonfirebasedata.firestore.FirestoreMigration
import com.gchristov.thecodinglove.commonfirebasedata.firestore.ScratchMigration
import com.gchristov.thecodinglove.commonkotlin.JsonSerializer
import com.gchristov.thecodinglove.commonkotlin.di.DiModule
import kotlinx.coroutines.Dispatchers
import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.bindSingleton
import org.kodein.di.instance

object CommonFirebaseDataModule : DiModule() {
    override fun name() = "common-firebase-data"

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
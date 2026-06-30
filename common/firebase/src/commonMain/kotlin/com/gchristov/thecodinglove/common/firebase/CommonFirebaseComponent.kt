package com.gchristov.thecodinglove.common.firebase

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.firebase.firestore.FirestoreMigration
import com.gchristov.thecodinglove.common.firebase.firestore.ScratchMigration
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.kotlin.di.Singleton
import kotlinx.coroutines.Dispatchers
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
interface CommonFirebaseComponent {
    @Provides
    @Singleton
    fun firebaseAdmin(): FirebaseAdmin = provideFirebaseAdmin()

    @Provides
    fun firestoreMigrations(
        log: Logger,
        firebaseAdmin: FirebaseAdmin,
        jsonSerializer: JsonSerializer.ExplicitNulls,
    ): List<FirestoreMigration> = listOf(
        ScratchMigration(
            dispatcher = Dispatchers.Default,
            log = log,
            firebaseAdmin = firebaseAdmin,
            jsonSerializer = jsonSerializer,
        ),
    )
}

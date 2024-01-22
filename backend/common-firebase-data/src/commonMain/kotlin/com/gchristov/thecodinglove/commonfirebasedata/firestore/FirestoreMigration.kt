package com.gchristov.thecodinglove.commonfirebasedata.firestore

import arrow.core.Either
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonfirebasedata.FirebaseAdmin
import com.gchristov.thecodinglove.commonkotlin.JsonSerializer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

interface FirestoreMigration {
    suspend fun invoke(): Either<Throwable, Unit>
}

/**
 * As of now, Firestore doesn't natively support migrations, so we have to manually perform them if needed. This
 * scratch file provides a template to do that, which will be run once every time the server restarts. To add a
 * migration you can:
 *
 * 1. Use the [firebaseAdmin] instance to load up data to migrate. Keep in mind that if the domain has changed you may
 * need to parse the existing data using the old domain models.
 * 2. Manipulate the data in-memory and prepare it for persistence.
 * 3. Overwrite the old documents with the new ones, using [FirestoreDocumentReference.set] with merge enabled.
 */
@Suppress("unused")
internal class ScratchMigration(
    private val dispatcher: CoroutineDispatcher,
    private val log: Logger,
    private val firebaseAdmin: FirebaseAdmin,
    private val jsonSerializer: JsonSerializer,
) : FirestoreMigration {
    private val tag = this::class.simpleName

    override suspend fun invoke(): Either<Throwable, Unit> = withContext(dispatcher) {
        // Write your migration here.
        Either.Right(Unit)
    }
}
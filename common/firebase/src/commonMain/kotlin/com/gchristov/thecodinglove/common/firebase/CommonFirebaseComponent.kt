package com.gchristov.thecodinglove.common.firebase

import com.gchristov.thecodinglove.common.kotlin.di.Singleton
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
interface CommonFirebaseComponent {
    @Provides
    @Singleton
    fun firebaseAdmin(): FirebaseAdmin = provideFirebaseAdmin()
}

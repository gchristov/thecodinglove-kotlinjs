package com.gchristov.thecodinglove.firebase

external interface UserInfo {
    val uid: String
    val displayName: String
    val email: String
    val phoneNumber: String
    val photoURL: String?
    val providerId: String
}

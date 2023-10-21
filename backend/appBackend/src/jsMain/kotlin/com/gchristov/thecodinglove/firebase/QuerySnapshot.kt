package com.gchristov.thecodinglove.firebase

external interface QuerySnapshot {
    val empty: Boolean
    val size: Int
    val docs: Array<DocumentSnapshot>
}
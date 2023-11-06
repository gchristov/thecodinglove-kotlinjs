package com.gchristov.thecodinglove.commonfirebasedata.firestore

import kotlinx.serialization.Serializable

@Serializable
data class TestFirestoreDoc(val something: Int, val another: List<String>?, val map: Map<String, String>?)
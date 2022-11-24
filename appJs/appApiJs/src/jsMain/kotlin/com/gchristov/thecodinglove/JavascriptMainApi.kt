package com.gchristov.thecodinglove

import com.gchristov.thecodinglove.kmpcommonfirebase.CommonFirebaseModule
import com.gchristov.thecodinglove.kmpsearch.SearchHistory
import com.gchristov.thecodinglove.kmpsearch.SearchModule
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

internal actual fun serveApi(args: Array<String>) {
    val fireFunctions = require("firebase-functions")
    exports.myTestFun = fireFunctions.https.onRequest { request, response ->
        // TODO: Do not use GlobalScope
        val searchQuery = request.query.searchQuery as? String
        GlobalScope.launch {
            println("About to test Firestore")
            val firestore = CommonFirebaseModule.injectFirestore()
            val document = firestore.document("preferences/user1").get()
            val count = document.get<Int>("count")
            println("Got Firestore document: exists=${document.exists}, count=$count")
            val batch = firestore.batch()
            batch.set(firestore.document("preferences/user1"), Count(count + 1))
            batch.commit()

            println("About to test search")
            val search = SearchModule.injectSearchUseCase()
            val searchResult = search(
                query = searchQuery ?: "release",
                searchHistory = SearchHistory(),
                resultsPerPage = 4
            )

            response.send("{\"invocationsCount\": $count, \"searchResult\": \"$searchResult\"}")
        }
    }
}

private external fun require(module: String): dynamic
private external var exports: dynamic

@Serializable
private data class Count(
    val count: Int
)
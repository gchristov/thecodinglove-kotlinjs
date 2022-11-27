package com.gchristov.thecodinglove.kmpsearchdata

import com.gchristov.thecodinglove.kmphtmlparse.HtmlPostParser
import com.gchristov.thecodinglove.kmpsearchdata.api.ApiSearchSession
import com.gchristov.thecodinglove.kmpsearchdata.model.*
import dev.gitlive.firebase.firestore.FirebaseFirestore
import io.ktor.client.statement.*

internal class RealSearchRepository(
    private val apiService: SearchApi,
    private val htmlPostParser: HtmlPostParser,
    private val firebaseFirestore: FirebaseFirestore
) : SearchRepository {
    override suspend fun getTotalPosts(query: String): Int {
        val response = apiService.search(
            // First page should always exist if there are results
            page = 1,
            query = query
        ).bodyAsText()
        return htmlPostParser.parseTotalPosts(response)
    }

    override suspend fun search(
        page: Int,
        query: String
    ): List<Post> {
        val response = apiService.search(
            page = page,
            query = query
        ).bodyAsText()
        return htmlPostParser.parsePosts(response).map { it.toPost() }
    }

    override suspend fun getSearchSession(id: String): SearchSession? {
        val document = firebaseFirestore.document("search/$id").get()
        return if (document.exists) {
            val apiSearchSession: ApiSearchSession = document.data()
            apiSearchSession.toSearchSession()
        } else {
            null
        }
    }

    override suspend fun saveSearchSession(searchSession: SearchSession) {
        val batch = firebaseFirestore.batch()
        val document = firebaseFirestore.document("search/${searchSession.id}")
        batch.set(document, searchSession.toApiSearchSession())
        batch.commit()
    }
}
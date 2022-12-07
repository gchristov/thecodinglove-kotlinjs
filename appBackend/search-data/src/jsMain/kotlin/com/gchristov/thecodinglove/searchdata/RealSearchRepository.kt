package com.gchristov.thecodinglove.searchdata

import arrow.core.Either
import com.gchristov.thecodinglove.htmlparse.HtmlPostParser
import com.gchristov.thecodinglove.searchdata.api.ApiSearchSession
import com.gchristov.thecodinglove.searchdata.model.*
import dev.gitlive.firebase.firestore.FirebaseFirestore
import io.ktor.client.statement.*

internal class RealSearchRepository(
    private val apiService: SearchApi,
    private val htmlPostParser: HtmlPostParser,
    private val firebaseFirestore: FirebaseFirestore
) : SearchRepository {
    override suspend fun getTotalPosts(query: String): Either<Exception, Int> {
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
    ): Either<Exception, List<Post>> {
        val response = apiService.search(
            page = page,
            query = query
        ).bodyAsText()
        return htmlPostParser.parsePosts(response).map { posts -> posts.map { it.toPost() } }
    }

    override suspend fun getSearchSession(id: String): SearchSession? {
        val document = firebaseFirestore
            .collection("searchSession")
            .document(id)
            .get()
        return if (document.exists) {
            val apiSearchSession: ApiSearchSession = document.data()
            apiSearchSession.toSearchSession()
        } else {
            null
        }
    }

    override suspend fun saveSearchSession(searchSession: SearchSession) {
        val document = firebaseFirestore
            .collection("searchSession")
            .document(searchSession.id)
        document.set(
            data = searchSession.toApiSearchSession(),
            encodeDefaults = true,
            merge = true
        )
    }
}
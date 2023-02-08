package com.gchristov.thecodinglove.searchdata

import com.gchristov.thecodinglove.htmlparse.HtmlPostParser
import com.gchristov.thecodinglove.kmpcommondi.DiModule
import com.gchristov.thecodinglove.searchdata.model.SearchConfig
import dev.gitlive.firebase.firestore.FirebaseFirestore
import io.ktor.client.*
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

object SearchDataModule : DiModule() {
    override fun name() = "kmp-search-data"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindSingleton { provideSearchApi(client = instance()) }
            bindSingleton {
                provideSearchRepository(
                    api = instance(),
                    htmlPostParser = instance(),
                    firebaseFirestore = instance()
                )
            }
            bindSingleton { provideSearchConfig() }
        }
    }

    private fun provideSearchApi(client: HttpClient) = SearchApi(client)

    private fun provideSearchRepository(
        api: SearchApi,
        htmlPostParser: HtmlPostParser,
        firebaseFirestore: FirebaseFirestore
    ): SearchRepository = RealSearchRepository(
        apiService = api,
        htmlPostParser = htmlPostParser,
        firebaseFirestore = firebaseFirestore
    )

    private fun provideSearchConfig(): SearchConfig = SearchConfig(postsPerPage = 4)
}
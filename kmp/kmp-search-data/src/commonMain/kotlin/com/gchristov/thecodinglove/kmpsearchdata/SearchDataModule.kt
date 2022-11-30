package com.gchristov.thecodinglove.kmpsearchdata

import com.gchristov.thecodinglove.kmpcommondi.DiModule
import com.gchristov.thecodinglove.kmpcommonfirebase.CommonFirebaseModule
import com.gchristov.thecodinglove.kmpcommonnetwork.CommonNetworkModule
import com.gchristov.thecodinglove.kmphtmlparse.HtmlParseModule
import com.gchristov.thecodinglove.kmphtmlparse.HtmlPostParser
import com.gchristov.thecodinglove.kmpsearchdata.model.SearchConfig
import dev.gitlive.firebase.firestore.FirebaseFirestore
import io.ktor.client.*
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

object SearchDataModule : DiModule() {
    override fun name() = "kmp-search-data"

    override fun bindLocalDependencies(builder: DI.Builder) {
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

    override fun moduleDependencies(): List<DI.Module> {
        return listOf(
            CommonNetworkModule.module,
            CommonFirebaseModule.module,
            HtmlParseModule.module
        )
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
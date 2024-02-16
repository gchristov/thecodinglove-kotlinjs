package com.gchristov.thecodinglove.selfdestruct.proto

import com.gchristov.thecodinglove.common.kotlin.di.DiModule
import com.gchristov.thecodinglove.common.network.NetworkClient
import com.gchristov.thecodinglove.selfdestruct.proto.http.RealSelfDestructServiceRepository
import com.gchristov.thecodinglove.selfdestruct.proto.http.SelfDestructServiceApi
import com.gchristov.thecodinglove.selfdestruct.proto.http.SelfDestructServiceRepository
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

class SelfDestructProtoModule(private val apiUrl: String) : DiModule() {
    override fun name() = "self-destruct-proto"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindSingleton {
                provideSelfDestructServiceApi(
                    networkClient = instance(),
                    apiUrl = apiUrl,
                )
            }
            bindSingleton {
                provideSelfDestructServiceRepository(selfDestructServiceApi = instance())
            }
        }
    }

    private fun provideSelfDestructServiceApi(
        networkClient: NetworkClient.Json,
        apiUrl: String,
    ): SelfDestructServiceApi = SelfDestructServiceApi(
        client = networkClient,
        apiUrl = apiUrl,
    )

    private fun provideSelfDestructServiceRepository(selfDestructServiceApi: SelfDestructServiceApi): SelfDestructServiceRepository =
        RealSelfDestructServiceRepository(selfDestructServiceApi = selfDestructServiceApi)
}
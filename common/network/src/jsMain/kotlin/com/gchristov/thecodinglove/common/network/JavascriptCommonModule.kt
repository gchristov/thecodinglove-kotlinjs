package com.gchristov.thecodinglove.common.network

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.network.http.ExpressHttpService
import com.gchristov.thecodinglove.common.network.http.HttpService

actual fun provideHttpService(log: Logger): HttpService = ExpressHttpService(log)
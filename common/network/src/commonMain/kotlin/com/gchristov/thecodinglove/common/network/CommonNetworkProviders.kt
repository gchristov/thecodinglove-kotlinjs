package com.gchristov.thecodinglove.common.network

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.network.http.HttpService

expect fun provideHttpService(log: Logger): HttpService

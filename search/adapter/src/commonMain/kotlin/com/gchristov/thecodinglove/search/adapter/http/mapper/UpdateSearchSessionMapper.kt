package com.gchristov.thecodinglove.search.adapter.http.mapper

import com.gchristov.thecodinglove.search.adapter.http.model.ApiUpdateSearchSessionState
import com.gchristov.thecodinglove.search.domain.model.SearchSession

internal fun ApiUpdateSearchSessionState.ApiState.toState() = when (this) {
    is ApiUpdateSearchSessionState.ApiState.Searching -> SearchSession.State.Searching()
    is ApiUpdateSearchSessionState.ApiState.SelfDestruct -> SearchSession.State.SelfDestruct()
    is ApiUpdateSearchSessionState.ApiState.Sent -> SearchSession.State.Sent()
}
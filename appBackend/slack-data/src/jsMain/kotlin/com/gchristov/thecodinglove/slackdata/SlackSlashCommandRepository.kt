package com.gchristov.thecodinglove.slackdata

import com.gchristov.thecodinglove.commonfirebase.FirebaseFunctionsResponse
import com.gchristov.thecodinglove.slackdata.domain.SlackSlashCommand

interface SlackSlashCommandRepository {
    fun observeSlashCommand(callback: (SlackSlashCommand?, FirebaseFunctionsResponse) -> Unit)
}
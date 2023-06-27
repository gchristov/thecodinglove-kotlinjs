package com.gchristov.thecodinglove.express

import com.gchristov.thecodinglove.commonservice.Request

/*
 * Full implementation: https://github.com/wadejensen/kotlin-nodejs-example
 */
internal class ExpressRequest(req: dynamic) : Request {
    override val baseURL: String = req.baseUrl as String

    //    val body: Map<String, String>? = req.body as? Json
//    val cookies: Map<String, String>? = null
    override val hostname: String = req.hostname as String
    override val ip: String = req.ip as String
    override val ips: Array<String>? = req.ips as? Array<String>
    override val method: String = req.method as String

    //    val parameters: Map<String, String>? = req.params as? Json
    override val path: String = req.path as String
    override val protocol: String = req.protocol as String
//    val query: Map<String, String>? = req.query as? Json
}
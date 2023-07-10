package com.gchristov.thecodinglove.express

import com.gchristov.thecodinglove.commonservice.HttpRequest
import com.gchristov.thecodinglove.commonservice.ParameterMap

/*
 * Full implementation: https://github.com/wadejensen/kotlin-nodejs-example
 */
internal class ExpressHttpRequest(private val req: dynamic) : HttpRequest {
    override val baseURL: String = req.baseUrl as String
    override val hostname: String = req.hostname as String
    override val ip: String = req.ip as String
    override val ips: Array<String>? = req.ips as? Array<String>
    override val method: String = req.method as String
    override val path: String = req.path as String
    override val protocol: String = req.protocol as String
    override val headers: ParameterMap
        get() = object : ParameterMap {
            override fun <T> get(key: String): T? = req.headers[key] as? T
        }
    override val query: ParameterMap
        get() = object : ParameterMap {
            override fun <T> get(key: String): T? = req.query[key] as? T
        }
    override val body: Any? = req.body
    override val bodyString: String? = req.bodyString as? String
}
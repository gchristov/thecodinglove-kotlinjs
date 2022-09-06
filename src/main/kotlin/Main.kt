external fun require(module:String) : dynamic
external var exports: dynamic

fun main(args: Array<String>) {
    val fireFunctions = require("firebase-functions")
    exports.myTestFun = fireFunctions.https.onRequest { request , response ->
        response.send("Hello World!")
    }
}
package ren.lawliet.requests

import ren.lawliet.requests.Method.Get
import ren.lawliet.requests.Method.Post
import org.jetbrains.annotations.NotNull
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset

class Requests {
    val normal_header: MutableMap<String, String> = mutableMapOf(
        Pair("access-control-allow-origin", "*"),
        Pair(
            "User-Agent",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36 Edg/112.0.1722.58"
        )
    )
    var data: MutableMap<String, String> = mutableMapOf()
    var headers: MutableMap<String, String> = normal_header
    lateinit var url: String
    lateinit var method: Enum<Method>

    var status_code = 0

    private fun run(
        @NotNull url: String,
        method: Method,
        headers: MutableMap<String, String>? = null,
        data: MutableMap<String, String>? = null
    ): String {
        if (headers != null) this.headers = headers
        this.method = method
        this.url = url
        var connection = URL(url).openConnection() as HttpURLConnection
        var methodName: String = when (method) {
            Get -> "GET"
            Post -> "POST"
        }
        connection.requestMethod = methodName
        connection = this.header(connection)

        if (data != null && method == Post) {
            val os = connection.outputStream
            var postData = ""
            val iterator = data.iterator()
            while (iterator.hasNext()) {
                val (key, value) = iterator.next()
                if (iterator.hasNext()) {
                    postData = "$postData$key=$value"
                } else {
                    postData = "$postData$key=$value&"
                }
            }
            os.write(postData.toByteArray(Charset.forName("UTF-8")))
        }
        this.status_code = connection.responseCode

        return connection.inputStream.bufferedReader().use { it.readText() }
    }

    fun get(
        url: String,
        headers: MutableMap<String, String>? = null,
        data: MutableMap<String, String>? = null
    ): String {
        if (headers != null) this.headers = headers
        if (data != null) this.data = data
        return this.run(url, Get, headers)
    }

    fun post(
        url: String,
        headers: MutableMap<String, String>? = null,
        data: MutableMap<String, String>? = null
    ): String {
        if (headers != null) this.headers = headers
        if (data != null) this.data = data
        return this.run(url, Post, headers)
    }

    fun setHeader(key: String, value: String): Requests {
        this.headers[key] = value
        return this
    }

    fun setHeader(data: Pair<String, String>): Requests {
        this.headers[data.first] = data.second
        return this
    }

    fun setData(key: String, value: String): Requests {
        this.data[key] = value
        return this
    }

    fun setData(data: Pair<String, String>): Requests {
        this.data[data.first] = data.second
        return this
    }

    private fun header(connection: HttpURLConnection): HttpURLConnection {
        for ((key, value) in this.headers) {
            connection.setRequestProperty(key, value)
        }
        return connection
    }

    companion object {
        val user_agent = Pair(
            "User-Agent",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36 Edg/112.0.1722.58"
        )

    }
}
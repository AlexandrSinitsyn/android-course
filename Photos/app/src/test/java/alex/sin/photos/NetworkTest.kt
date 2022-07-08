package alex.sin.photos

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL


class NetworkTest {

    private val IMAGES_COUNT = 30

    private var urls: MutableList<String> = mutableListOf()

    fun findPhotos() {
        log("Photos", "start searching")

        val input =
            URL("https://api.unsplash.com/photos/random?client_id=IUfizx4eBtvLJS1H7GrGJXWdHWzSL-PV7dQcOAH-HS4&count=$IMAGES_COUNT")
                .openStream().bufferedReader()

        log("Photos", "got the json")

        val request = StringBuilder()
        input.lines().forEach { request.append(it) }

        val outerArray = JSONArray(request.toString())//Gson().fromJson<List>(request.toString(), object : TypeToken<List>() {}.type)

        val json =
            """[{"name":"John"},{"name":"Jane"},{"name":"William"}]"""
        val authors = Gson().fromJson<List<String>>(json, String::class.java)

        log("Photos", "found urls")

        for (i in 0 until outerArray.length()) {
            val urlSmall = outerArray.getJSONObject(i).getJSONObject("urls").getString("full")

            log("Photos", "cur url: $urlSmall")

            urls.add(urlSmall)
        }

        log("Photos", "saved")
    }

    private fun log(tag: String, msg: String) = println("$tag: $msg")
}

fun main() {
    NetworkTest().findPhotos()
}

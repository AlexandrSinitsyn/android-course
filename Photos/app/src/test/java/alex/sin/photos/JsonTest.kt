package alex.sin.photos

import org.json.JSONArray
import java.lang.Exception

object JsonTest {
    @Throws(Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val json = "[\"9784142605765949528\",2869,264,7]"
        val result = JSONArray(json)
        println(result)
    }
}
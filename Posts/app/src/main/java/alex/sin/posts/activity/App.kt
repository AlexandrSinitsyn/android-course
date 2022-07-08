package alex.sin.posts.activity

import alex.sin.posts.domain.Post
import alex.sin.posts.service.PostRepository
import android.app.Application
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class App: Application() {

    lateinit var postRepository: PostRepository
        private set
    var posts = mutableListOf<Post>()
    var mainActivity: MainActivity? = null
    var exception: Throwable? = null
    var onCreate = true

    override fun onCreate() {
        super.onCreate()
        instance = this
        val client = OkHttpClient()
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val retrofit = Retrofit.Builder()
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .build()
        postRepository = retrofit.create(PostRepository::class.java)
    }

    companion object {
        lateinit var instance: App
            private set
    }
}

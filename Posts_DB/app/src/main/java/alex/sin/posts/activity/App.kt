package alex.sin.posts.activity

import alex.sin.posts.domain.Post
import alex.sin.posts.service.AppDatabase
import alex.sin.posts.service.PostRepository
import android.app.Application
import androidx.room.Room
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class App: Application() {

    lateinit var postRepository: PostRepository
        private set
    lateinit var appDatabase: AppDatabase
        private set
    var mainActivity: MainActivity? = null
    var exception: Throwable? = null
    var onCreate = true
        private set

    fun activate() {
        onCreate = true
    }

    fun triggerOff() {
        onCreate = false
    }

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
        appDatabase = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "PostsDatabase").build()
    }

    companion object {
        lateinit var instance: App
            private set
    }
}

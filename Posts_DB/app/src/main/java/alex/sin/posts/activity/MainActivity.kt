@file:Suppress("BlockingMethodInNonBlockingContext", "DEPRECATION")

package alex.sin.posts.activity

import alex.sin.posts.R
import alex.sin.posts.databinding.ActivityMainBinding
import alex.sin.posts.domain.Post
import alex.sin.posts.model.PostsViewModel
import alex.sin.posts.service.PostAdapter
import alex.sin.posts.service.PostService.clearPostDb
import alex.sin.posts.service.PostService.countPostsInDb
import alex.sin.posts.service.PostService.erasePostFromDb
import alex.sin.posts.service.PostService.getAllPostsFormDb
import alex.sin.posts.service.PostService.insertAllPostsToDb
import alex.sin.posts.service.PostService.insertPostToDb
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val DEBUG = true

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: PostAdapter

    private val postsViewModel: PostsViewModel by viewModels()

    private val postObservable = object : Observable<Post>() {
        override fun subscribeActual(observer: Observer<in Post>?) {  }
    }

    private val listOfRequests = mutableListOf<Call<out Any>>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        App.instance.mainActivity = this

        postObservable.subscribeOn(Schedulers.single()).subscribe(postsViewModel.postObserver)

        postsViewModel.postObserver.onNext()

        setContentView(R.layout.activity_intro)

        if (downloadAllPosts()) {
            setContentView(binding.root)

            binding.addPost.setOnClickListener {
                startActivity(Intent(this@MainActivity, PostCreationActivity::class.java))
            }

            binding.refresh.setOnClickListener {
                App.instance.activate()

                if (!downloadAllPosts()) {
                    App.instance.triggerOff()
                }
            }
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        lifecycleScope.launch {
            val viewManager = LinearLayoutManager(this@MainActivity)
            binding.allPosts.apply {
                layoutManager = viewManager
                adapter = PostAdapter(getAllPostsFormDb().toMutableList()) {
                    deletePost(it)
                }

                this@MainActivity.adapter = adapter!! as PostAdapter
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        if (downloadAllPosts()) {
            setContentView(binding.root)

            binding.addPost.setOnClickListener {
                startActivity(Intent(this@MainActivity, PostCreationActivity::class.java))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        listOfRequests.forEach { it.cancel() }
    }


    private fun downloadAllPosts(): Boolean {
        interruptedConnection() ?: return false

        if (!App.instance.onCreate) {
            return true
        }

        lifecycleScope.launch {
            clearPostDb()
            App.instance.triggerOff()

            val call = App.instance.postRepository.getAllPosts()

            call.enqueue(object : Callback<List<Post>> {
                override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                    val allPosts = response.body()!!.toMutableList().let { if (DEBUG) it.subList(0, 5) else it }.toMutableList()

                    lifecycleScope.launch {
                        insertAllPostsToDb(allPosts)
                    }

                    val viewManager = LinearLayoutManager(this@MainActivity)
                    binding.allPosts.apply {
                        layoutManager = viewManager
                        adapter = PostAdapter(allPosts) {
                            deletePost(it)
                        }

                        this@MainActivity.adapter = adapter!! as PostAdapter
                    }
                }

                override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                    failureOccurred(t)
                }
            })

            listOfRequests.add(call)
        }

        return true
    }

    fun addPost(userId: Int, title: String, body: String) {
        interruptedConnection() ?: return

        var newPost = Post(userId, -1, title, body)

        val call = App.instance.postRepository.addPost(newPost)

        call.enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                newPost = response.body()!!

                lifecycleScope.launch {
                    insertPostToDb(newPost)

                    adapter.insertItem(newPost, adapter.itemCount)
                }
            }

            override fun onFailure(call: Call<Post>, t: Throwable) = failureOccurred(t)
        })

        listOfRequests.add(call)
    }

    private fun deletePost(post: Post) {
        interruptedConnection() ?: return

        lifecycleScope.launch {
            val position = getAllPostsFormDb().indexOf(post)

            if (position < 0 || position >= countPostsInDb()) {
                Toast.makeText(this@MainActivity, "Not so fast! You are trying to delete already deleted post", Toast.LENGTH_SHORT).show()
            } else {
                val call = App.instance.postRepository.deletePost(post.id)

                call.enqueue(object : Callback<Post> {
                    override fun onResponse(call: Call<Post>, response: Response<Post>) {
                        lifecycleScope.launch {
                            erasePostFromDb(post)

                            adapter.eraseItem(position)
                        }
                    }

                    override fun onFailure(call: Call<Post>, t: Throwable) = failureOccurred(t)
                })

                listOfRequests.add(call)
            }
        }
    }


    private fun failureOccurred(t: Throwable) {
        App.instance.exception = t
        startActivity(Intent(this@MainActivity, FailureActivity::class.java))
    }

    private fun interruptedConnection(): Any? {
        if (isNetworkNotConnected()) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
            return null
        }

        return false
    }

    private fun isNetworkNotConnected(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return connectivityManager.activeNetworkInfo == null || !connectivityManager.activeNetworkInfo!!.isConnected
    }
}
@file:Suppress("BlockingMethodInNonBlockingContext")

package alex.sin.posts.activity

import alex.sin.posts.R
import alex.sin.posts.databinding.ActivityMainBinding
import alex.sin.posts.domain.Post
import alex.sin.posts.service.PostAdapter
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

const val DEBUG = true

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    private val listOfRequests = mutableListOf<Call<out Any>>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        App.instance.mainActivity = this

        setContentView(R.layout.activity_intro)

        if (getAllPosts()) {
            setContentView(binding.root)

            binding.addPost.setOnClickListener {
                startActivity(Intent(this@MainActivity, PostCreationActivity::class.java))
            }
        } else {
            Toast.makeText(this@MainActivity, "No internet connection", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        val viewManager = LinearLayoutManager(this@MainActivity)
        binding.allPosts.apply {
            layoutManager = viewManager
            adapter = PostAdapter(App.instance.posts) {
                deletePost(it)
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        if (getAllPosts()) {
            setContentView(binding.root)

            binding.addPost.setOnClickListener {
                startActivity(Intent(this@MainActivity, PostCreationActivity::class.java))
            }
        } else {
            Toast.makeText(this@MainActivity, "No internet connection", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        listOfRequests.forEach { it.cancel() }
    }


    private fun getAllPosts(): Boolean {
        interruptedConnection() ?: return false

        if (!App.instance.onCreate) {
            return true
        }

        App.instance.onCreate = false

        val call = App.instance.postRepository.getAllPosts()

        call.enqueue(object : Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                val allPosts = response.body()!!.toMutableList()

                App.instance.posts = if (DEBUG) allPosts.subList(0, 10) else allPosts

                val viewManager = LinearLayoutManager(this@MainActivity)
                binding.allPosts.apply {
                    layoutManager = viewManager
                    adapter = PostAdapter(App.instance.posts) {
                        deletePost(it)
                    }
                }
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                failureOccurred(t)
            }
        })

        listOfRequests.add(call)

        return true
    }

    fun addPost(userId: Int, title: String, body: String) {
        interruptedConnection() ?: return

        var newPost = Post(userId, -1, title, body)

        val call = App.instance.postRepository.addPost(newPost)

        call.enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                newPost = response.body()!!

                App.instance.posts.add(newPost)

                binding.allPosts.adapter?.notifyItemInserted(newPost.id - 1)
            }

            override fun onFailure(call: Call<Post>, t: Throwable) = failureOccurred(t)
        })

        listOfRequests.add(call)
    }

    private fun deletePost(post: Post) {
        interruptedConnection() ?: return

        val position = App.instance.posts.indexOf(post)

        if (position < 0 || position >= App.instance.posts.size) {
            Toast.makeText(this@MainActivity, "Not so fast! You are trying to delete already deleted post", Toast.LENGTH_SHORT).show()
            return
        }

        App.instance.postRepository.deletePost(post.id)

        App.instance.posts.removeAt(position)

        binding.allPosts.adapter?.notifyItemRemoved(position)
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
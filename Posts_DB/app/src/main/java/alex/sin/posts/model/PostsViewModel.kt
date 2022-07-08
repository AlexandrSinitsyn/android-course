@file:Suppress("BlockingMethodInNonBlockingContext", "DEPRECATION")

package alex.sin.posts.model

import alex.sin.posts.R
import alex.sin.posts.activity.App
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostsViewModel : ViewModel() {

    val postObserver = object : Observer<Post> {
        override fun onSubscribe(d: Disposable) {
            Log.d("onSubscribe", d.toString())
        }
        override fun onNext(t: String) {
            Log.d("onNext", "onNext: $t")
        }
        override fun onError(e: Throwable) {
            Log.d("onError", e.toString())
        }
        override fun onComplete() {
            Log.d("onComplete", "DONE!!")
        }
    }
}
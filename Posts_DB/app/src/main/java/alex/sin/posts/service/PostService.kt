package alex.sin.posts.service

import alex.sin.posts.activity.App
import alex.sin.posts.domain.Post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object PostService {

    suspend fun getPostFromDb(id: Int) =
        withContext(Dispatchers.IO) { App.instance.appDatabase.postDao().get(id) }

    suspend fun getAllPostsFormDb() =
        withContext(Dispatchers.IO) { App.instance.appDatabase.postDao().getAll() }

    suspend fun countPostsInDb() =
        withContext(Dispatchers.IO) { App.instance.appDatabase.postDao().count() }

    suspend fun insertPostToDb(post: Post) =
        withContext(Dispatchers.IO) { App.instance.appDatabase.postDao().insert(post) }

    suspend fun insertAllPostsToDb(posts: List<Post>) = withContext(Dispatchers.IO) {
        App.instance.appDatabase.postDao().insert(*posts.toTypedArray())
    }

    suspend fun erasePostFromDb(post: Post) =
        withContext(Dispatchers.IO) { App.instance.appDatabase.postDao().deletePost(post) }

    suspend fun clearPostDb() =
        withContext(Dispatchers.IO) { App.instance.appDatabase.postDao().deleteAll() }
}
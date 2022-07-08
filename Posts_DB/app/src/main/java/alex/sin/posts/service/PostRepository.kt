package alex.sin.posts.service

import alex.sin.posts.domain.Post
import retrofit2.Call
import retrofit2.http.*

interface PostRepository {

    @GET("posts")
    fun getAllPosts(): Call<List<Post>>

    @POST("posts")
    fun addPost(@Body post: Post): Call<Post>

    @DELETE("posts/{id}")
    fun deletePost(@Path("id") id: Int): Call<Post>
}
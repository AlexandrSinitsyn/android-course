package alex.sin.posts.service

import alex.sin.posts.domain.Post
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PostsDao {

    @Query("SELECT * FROM post WHERE id=:postId")
    suspend fun get(postId: Int): List<Post>

    @Query("SELECT * FROM post")
    suspend fun getAll(): List<Post>

    @Query("SELECT COUNT(*) FROM post")
    suspend fun count(): Int

    @Insert
    suspend fun insert(vararg posts: Post)

    @Delete
    suspend fun deletePost(post: Post)

    @Query("DELETE FROM post")
    suspend fun deleteAll()
}
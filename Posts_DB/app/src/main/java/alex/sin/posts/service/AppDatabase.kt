package alex.sin.posts.service

import alex.sin.posts.domain.Post
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Post::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun postDao(): PostsDao
}
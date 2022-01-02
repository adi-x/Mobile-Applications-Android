package com.app.todo.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.app.todo.data.Post

@Dao
interface PostDao {
    @Query("SELECT * from posts ORDER BY id ASC")
    fun getAll(): LiveData<List<Post>>

    @Query("SELECT * FROM posts WHERE id=:id ")
    fun getById(id: Long): LiveData<Post>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: Post)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(post: Post)

    @Query("DELETE FROM posts")
    suspend fun deleteAll()
}
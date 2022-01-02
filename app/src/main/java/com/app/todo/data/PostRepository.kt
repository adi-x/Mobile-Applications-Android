package com.app.todo.data

import android.util.Log
import androidx.lifecycle.LiveData
import com.app.core.TAG
import com.app.core.Result
import com.app.todo.data.local.PostDao
import com.app.todo.data.remote.PostApi

class PostRepository(private val postDao: PostDao) {

    val posts = postDao.getAll()

    suspend fun refresh(): Result<Boolean> {
        try {
            val posts = PostApi.service.find()
            for (post in posts) {
                postDao.insert(post)
            }
            return Result.Success(true)
        } catch(e: Exception) {
            return Result.Error(e)
        }
    }

    fun getById(postId: Long): LiveData<Post> {
        return postDao.getById(postId)
    }

    suspend fun save(post: Post): Result<Post> {
        try {
            val createdPost = PostApi.service.create(post)
            postDao.insert(createdPost)
            return Result.Success(createdPost)
        } catch(e: Exception) {
            return Result.Error(e)
        }
    }

    suspend fun update(post: Post): Result<Post> {
        try {
            val updatedPost = PostApi.service.update(post)
            postDao.update(updatedPost)
            return Result.Success(updatedPost)
        } catch(e: Exception) {
            return Result.Error(e)
        }
    }
}
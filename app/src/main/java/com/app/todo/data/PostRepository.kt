package com.app.todo.data

import android.util.Log
import com.app.core.TAG
import com.app.core.Result

object PostRepository {
    private var cachedPosts: MutableList<Post>? = null;

    suspend fun loadAll(): Result<List<Post>> {
        if (cachedPosts != null) {
            Log.v(TAG, "loadAll - return cached posts")
            return Result.Success(cachedPosts as List<Post>);
        }
        try {
            Log.v(TAG, "loadAll - started")
            val posts = PostApi.service.find()
            Log.v(TAG, "loadAll - succeeded")
            cachedPosts = mutableListOf()
            cachedPosts?.addAll(posts)
            return Result.Success(cachedPosts as List<Post>)
        } catch (e: Exception) {
            Log.w(TAG, "loadAll - failed", e)
            return Result.Error(e)
        }
    }

    suspend fun load(postId: Long): Result<Post> {
        val post = cachedPosts?.find { it.id == postId }
        if (post != null) {
            Log.v(TAG, "load - return cached post")
            return Result.Success(post)
        }
        try {
            Log.v(TAG, "load - started")
            val postRead = PostApi.service.read(postId)
            Log.v(TAG, "load - succeeded")
            return Result.Success(postRead)
        } catch (e: Exception) {
            Log.w(TAG, "load - failed", e)
            return Result.Error(e)
        }
    }

    suspend fun save(post: Post): Result<Post> {
        try {
            Log.v(TAG, "save - started")
            val createdPost = PostApi.service.create(post)
            Log.v(TAG, "save - succeeded")
            cachedPosts?.add(createdPost)
            return Result.Success(createdPost)
        } catch (e: Exception) {
            Log.w(TAG, "save - failed", e)
            return Result.Error(e)
        }
    }

    suspend fun update(post: Post): Result<Post> {
        try {
            Log.v(TAG, "update - started")
            val updatedPost = PostApi.service.update(post)
            val index = cachedPosts?.indexOfFirst { it.id == post.id }
            if (index != null) {
                cachedPosts?.set(index, updatedPost)
            }
            Log.v(TAG, "update - succeeded")
            return Result.Success(updatedPost)
        } catch (e: Exception) {
            Log.v(TAG, "update - failed")
            return Result.Error(e)
        }
    }
}
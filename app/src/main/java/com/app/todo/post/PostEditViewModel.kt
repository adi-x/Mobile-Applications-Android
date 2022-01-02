package com.app.todo.post

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.app.todo.data.PostRepository
import com.app.core.TAG
import com.app.todo.data.Post
import com.app.core.Result
import com.app.todo.data.local.TodoDatabase
import kotlinx.coroutines.launch

class PostEditViewModel(application: Application) : AndroidViewModel(application) {
    private val mutableFetching = MutableLiveData<Boolean>().apply { value = false }
    private val mutableCompleted = MutableLiveData<Boolean>().apply { value = false }
    private val mutableException = MutableLiveData<Exception>().apply { value = null }

    val fetching: LiveData<Boolean> = mutableFetching
    val fetchingError: LiveData<Exception> = mutableException
    val completed: LiveData<Boolean> = mutableCompleted

    val postRepository: PostRepository

    init {
        val postDao = TodoDatabase.getDatabase(application, viewModelScope).postDao()
        postRepository = PostRepository(postDao)
    }

    fun getItemById(postId: Long): LiveData<Post> {
        Log.v(TAG, "getItemById...")
        return postRepository.getById(postId)
    }

    fun saveOrUpdatePost(post: Post) {
        viewModelScope.launch {
            Log.v(TAG, "saveOrUpdatePost...");
            mutableFetching.value = true
            mutableException.value = null
            val result: Result<Post>
            if (post.id != -1L) {
                result = postRepository.update(post)
            } else {
                result = postRepository.save(post)
            }
            when (result) {
                is Result.Success -> {
                    Log.d(TAG, "saveOrUpdatePost succeeded");
                }
                is Result.Error -> {
                    Log.w(TAG, "saveOrUpdatePost failed", result.exception);
                    mutableException.value = result.exception
                }
            }
            mutableCompleted.value = true
            mutableFetching.value = false
        }
    }
}

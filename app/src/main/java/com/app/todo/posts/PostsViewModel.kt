package com.app.todo.posts

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.app.todo.data.PostRepository
import com.app.core.TAG
import com.app.core.Result
import com.app.todo.data.Post
import com.app.todo.data.local.TodoDatabase
import kotlinx.coroutines.launch

class PostListViewModel(application: Application) : AndroidViewModel(application) {
    private val mutableLoading = MutableLiveData<Boolean>().apply { value = false }
    private val mutableException = MutableLiveData<Exception>().apply { value = null }

    val posts: LiveData<List<Post>>
    val loading: LiveData<Boolean> = mutableLoading
    val loadingError: LiveData<Exception> = mutableException

    val postRepository: PostRepository

    init {
        val postDao = TodoDatabase.getDatabase(application, viewModelScope).postDao()
        postRepository = PostRepository(postDao)
        posts = postRepository.posts
    }

    fun refresh() {
        viewModelScope.launch {
            Log.v(TAG, "refresh...")
            mutableLoading.value = true
            mutableException.value = null
            when (val result = postRepository.refresh()) {
                is Result.Success -> {
                    Log.d(TAG, "refresh succeeded")
                }
                is Result.Error -> {
                    Log.w(TAG, "refresh failed", result.exception)
                    mutableException.value = result.exception
                }
            }
            mutableLoading.value = false
        }
    }
}

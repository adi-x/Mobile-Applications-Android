package com.app.todo.post

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.todo.data.PostRepository
import com.app.core.TAG
import com.app.todo.data.Post
import com.app.core.Result
import kotlinx.coroutines.launch

class PostEditViewModel : ViewModel() {
    private val mutablePost = MutableLiveData<Post>().apply { value = Post(-1, "", "", "", "", .0, .0) }
    private val mutableFetching = MutableLiveData<Boolean>().apply { value = false }
    private val mutableCompleted = MutableLiveData<Boolean>().apply { value = false }
    private val mutableException = MutableLiveData<Exception>().apply { value = null }

    val post: LiveData<Post> = mutablePost
    val fetching: LiveData<Boolean> = mutableFetching
    val fetchingError: LiveData<Exception> = mutableException
    val completed: LiveData<Boolean> = mutableCompleted

    fun loadPost(postId: Long) {
        viewModelScope.launch {
            Log.i(TAG, "loadPost...")
            mutableFetching.value = true
            mutableException.value = null
            when (val result = PostRepository.load(postId)) {
                is Result.Success -> {
                    Log.d(TAG, "loadPost succeeded");
                    mutablePost.value = result.data
                }
                is Result.Error -> {
                    Log.w(TAG, "loadPost failed", result.exception);
                    mutableException.value = result.exception
                }
            }
            mutableFetching.value = false
        }
    }

    fun saveOrUpdatePost(imageUrl: String, title: String, description: String) {
        viewModelScope.launch {
            Log.v(TAG, "saveOrUpdatePost...");
            val post = mutablePost.value ?: return@launch

            post.imageUrl = imageUrl
            post.title = title
            post.description = description

            mutableFetching.value = true
            mutableException.value = null
            val result: Result<Post>
            if (post.id != -1L) {
                result = PostRepository.update(post)
            } else {
                result = PostRepository.save(post)
            }
            when (result) {
                is Result.Success -> {
                    Log.d(TAG, "saveOrUpdatePost succeeded");
                    mutablePost.value = result.data
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

package com.app.todo.posts

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.todo.data.PostRepository
import com.app.core.TAG
import com.app.core.Result
import com.app.todo.data.Post
import kotlinx.coroutines.launch

class PostListViewModel : ViewModel() {
    private val mutablePosts = MutableLiveData<List<Post>>().apply { value = emptyList() }
    private val mutableLoading = MutableLiveData<Boolean>().apply { value = false }
    private val mutableException = MutableLiveData<Exception>().apply { value = null }

    val posts: LiveData<List<Post>> = mutablePosts
    val loading: LiveData<Boolean> = mutableLoading
    val loadingError: LiveData<Exception> = mutableException

    fun createPost(position: Int): Unit {
        val list = mutableListOf<Post>()
        list.addAll(mutablePosts.value!!)
        mutablePosts.value = list
    }

    fun loadPosts() {
        viewModelScope.launch {
            Log.v(TAG, "loadPosts...");
            mutableLoading.value = true
            mutableException.value = null
            when (val result = PostRepository.loadAll()) {
                is Result.Success -> {
                    Log.d(TAG, "loadPosts succeeded");
                    mutablePosts.value = result.data
                }
                is Result.Error -> {
                    Log.w(TAG, "loadPosts failed", result.exception);
                    mutableException.value = result.exception
                }
            }
            mutableLoading.value = false
        }
    }
}

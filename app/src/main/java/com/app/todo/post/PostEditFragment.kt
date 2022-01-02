package com.app.todo.post

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.app.core.TAG
import com.app.databinding.FragmentPostEditBinding
import com.app.todo.data.Post

class PostEditFragment : Fragment() {
    companion object {
        const val POST_ID = "POST_ID"
    }

    private lateinit var viewModel: PostEditViewModel
    private var postId: Long? = null
    private var post: Post? = null

    private var _binding: FragmentPostEditBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i(TAG, "onCreateView")
        arguments?.let {
            if (it.containsKey(POST_ID)) {
                postId = it.getLong(POST_ID)
            }
        }
        _binding = FragmentPostEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(TAG, "onViewCreated")
        setupViewModel()
        binding.fab.setOnClickListener {
            Log.v(TAG, "save post")
            val _post = post
            if (_post != null) {
                _post.title = binding.postTitle.text.toString()
                _post.description = binding.postDescription.text.toString()
                _post.imageUrl = binding.postImageUrl.text.toString()
                viewModel.saveOrUpdatePost(_post)
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.i(TAG, "onDestroyView")
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(PostEditViewModel::class.java)
        viewModel.fetching.observe(viewLifecycleOwner, { fetching ->
            Log.v(TAG, "update fetching")
            binding.progress.visibility = if (fetching) View.VISIBLE else View.GONE
        })
        viewModel.fetchingError.observe(viewLifecycleOwner, { exception ->
            if (exception != null) {
                Log.v(TAG, "update fetching error")
                val message = "Fetching exception ${exception.message}"
                val parentActivity = activity?.parent
                if (parentActivity != null) {
                    Toast.makeText(parentActivity, message, Toast.LENGTH_SHORT).show()
                }
            }
        })
        viewModel.completed.observe(viewLifecycleOwner, { completed ->
            if (completed) {
                Log.v(TAG, "completed, navigate back")
                findNavController().navigateUp()
            }
        })
        val id = postId
        if (id == null) {
            post = Post(-1, "", "", "", "", -1.0, -1.0)
        } else {
            viewModel.getItemById(id).observe(viewLifecycleOwner, {
                Log.v(TAG, "update items")
                if (it != null) {
                    post = it
                    binding.postTitle.setText(it.title)
                    binding.postDescription.setText(it.description)
                    binding.postImageUrl.setText(it.imageUrl)
                }
            })
        }
    }
}
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

class PostEditFragment : Fragment() {
    companion object {
        const val POST_ID = "POST_ID"
    }

    private lateinit var viewModel: PostEditViewModel
    private var postId: Long? = null

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
            viewModel.saveOrUpdatePost(
                binding.postImageUrl.text.toString(),
                binding.postTitle.text.toString(),
                binding.postDescription.text.toString()
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.i(TAG, "onDestroyView")
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(PostEditViewModel::class.java)
        viewModel.post.observe(viewLifecycleOwner, { post ->
            Log.v(TAG, "update posts")
            binding.postImageUrl.setText(post.imageUrl);
            binding.postTitle.setText(post.title);
            binding.postDescription.setText(post.description);
        })
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
        if (id != null) {
            viewModel.loadPost(id)
        }
    }
}
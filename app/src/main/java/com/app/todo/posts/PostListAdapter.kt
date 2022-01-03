package com.app.todo.posts

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.app.R
import com.app.core.TAG
import com.app.todo.data.Post
import com.app.todo.post.PostEditFragment

class PostListAdapter(
    private val fragment: Fragment,
) : RecyclerView.Adapter<PostListAdapter.ViewHolder>() {

    var posts = emptyList<Post>()
        set(value) {
            field = value
            notifyDataSetChanged();
        }

    private var onPostClick: View.OnClickListener = View.OnClickListener { view ->
        val post = view.tag as Post
        fragment.findNavController().navigate(R.id.action_PostListFragment_to_PostEditFragment, Bundle().apply {
            putLong(PostEditFragment.POST_ID, post.id)
        })
    };

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_post, parent, false)
        Log.v(TAG, "onCreateViewHolder")
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.v(TAG, "onBindViewHolder $position")
        val post = posts[position]
        holder.textView.text = post.title
        holder.itemView.tag = post
        holder.itemView.setOnClickListener(onPostClick)
    }

    override fun getItemCount() = posts.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView

        init {
            textView = view.findViewById(R.id.text)
        }
    }
}

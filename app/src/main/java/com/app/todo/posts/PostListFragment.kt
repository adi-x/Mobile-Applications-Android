package com.app.todo.posts

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.app.R
import com.app.auth.data.AuthRepository
import com.app.core.TAG
import com.app.databinding.FragmentPostListBinding
import androidx.work.*
import kotlin.random.Random

class PostListFragment : Fragment(), SensorEventListener {
    private var _binding: FragmentPostListBinding? = null
    private lateinit var postListAdapter: PostListAdapter
    private lateinit var postsModel: PostListViewModel
    private val binding get() = _binding!!
    private var latestLightReading: Float = 0f;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i(TAG, "onCreateView")
        _binding = FragmentPostListBinding.inflate(inflater, container, false)
        val sensorManager: SensorManager = activity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        light?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        return binding.root
    }

    private fun startAndObserveJob() {
        val constraints = androidx.work.Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val x: Int = Random.nextInt(5, 25)
        val inputData = Data.Builder()
            .putInt("delay", x)
            .putFloat("light", latestLightReading)
            .build()
        val myWork = OneTimeWorkRequest.Builder(NotifWorker::class.java)
            .setConstraints(constraints)
            .setInputData(inputData)
            .build()
        val workId = myWork.id
        context?.let {
            WorkManager.getInstance(it).apply {
                // enqueue Work
                enqueue(myWork)
                // observe work status
                getWorkInfoByIdLiveData(workId)
                    .observe(viewLifecycleOwner, { status ->
                        val isFinished = status?.state?.isFinished
                        Log.d(TAG, "Job $workId; finished: $isFinished")
                    })
            }
        }
        Toast.makeText(context, "Started a background task which will send you a notification in $x seconds.", Toast.LENGTH_SHORT).show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(TAG, "onViewCreated")
        if (!AuthRepository.isLoggedIn) {
            findNavController().navigate(R.id.FragmentLogin)
            return;
        }
        setupPostList()
        binding.fab.setOnClickListener {
            Log.v(TAG, "add new post")
            findNavController().navigate(R.id.PostEditFragment)
        }
        binding.fab2.setOnClickListener {
            Log.v(TAG, "Scheduling a notification.")
            startAndObserveJob()
        }
    }

    private fun setupPostList() {
        postListAdapter = PostListAdapter(this)
        binding.postList.adapter = postListAdapter
        postsModel = ViewModelProvider(this).get(PostListViewModel::class.java)
        postsModel.posts.observe(viewLifecycleOwner, { value ->
            Log.i(TAG, "update posts")
            postListAdapter.posts = value
        })
        postsModel.loading.observe(viewLifecycleOwner, { loading ->
            Log.i(TAG, "update loading")
            binding.progress.visibility = if (loading) View.VISIBLE else View.GONE
        })
        postsModel.loadingError.observe(viewLifecycleOwner, { exception ->
            if (exception != null) {
                Log.i(TAG, "update loading error")
                val message = "Loading exception ${exception.message}"
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
            }
        })
        postsModel.refresh()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.i(TAG, "onDestroyView")
        _binding = null
    }

    override fun onSensorChanged(p0: SensorEvent) {
        latestLightReading = p0.values[0];
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        Log.d(TAG,"Light sensor is reporting accuracy: $p1")
    }
}
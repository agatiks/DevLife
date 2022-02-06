package com.example.shevchenko

import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.shevchenko.databinding.ActivityMainBinding
import com.example.shevchenko.models.Post

import com.example.shevchenko.models.PostCached
import com.example.shevchenko.services.CacheService
import com.example.shevchenko.services.PostService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var database: CacheService
    private lateinit var service: PostService
    private var postList: MutableList<PostCached> = mutableListOf()
    private var currPost: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        database = GlobalApp.instance.databaseService
        service = GlobalApp.instance.service
        lifecycle.coroutineScope.launch {
            start()
        }
        binding.previous.setOnClickListener{
            currPost = maxOf(currPost - 1, 0)
            show(currPost)
        }
        binding.next.setOnClickListener{
            currPost++
            if(currPost == postList.size) {
                lifecycle.coroutineScope.launch {
                    new()
                }
            } else {
                show(currPost)
            }
        }

    }

    private suspend  fun new() {
        showProcessing()
        assert(currPost == postList.size)
        val response = service.getPost()
        if(response.isSuccessful && response.body() != null) {
            val post = PostCached(currPost, response.body()!!)
            withContext(Dispatchers.Default) { addToPostList(post) }
            database.insertAll(mutableListOf(post))
            Log.i("url", post.url)
        } else {
            currPost--
            binding.description.text = "Произошла ошибка загрузки"
            Glide.with(GlobalApp.instance)
                .load(R.drawable.network_error)
                .override(binding.gif.width)
                .into(binding.gif)
        }
        show(currPost)
    }

    private fun showProcessing() {
        binding.processing.visibility = View.VISIBLE
        binding.gif.visibility = View.GONE
        binding.description.visibility = View.GONE
    }

    private suspend fun start() {
        val fetchDBJob = lifecycle.coroutineScope.async {
            val postList = database.allCachedPosts() //взяли все закэшированные посты
            withContext(Dispatchers.Default) { updatePostList(postList) } // обновили массив постов в текущем запуске рпиложения,
            // так как кажется, что ходить каждый раз в базу будет тяжелее, чем брать данные из массива
            if (postList.size == 0) {
                //если первое взаимодействие с приложением, то добавляем пост в бд и в массив
                val response = service.getPost()
                if(response.isSuccessful && response.body()!=null) {
                    val post = PostCached(0, response.body()!!)
                    withContext(Dispatchers.Default) { addToPostList(post) }
                    database.insertAll(mutableListOf(post))
                    Log.i("url", post.url)
                } else {
                    binding.description.text = "Произошла ошибка загрузки"
                    Glide.with(GlobalApp.instance)
                        .load(R.drawable.network_error)
                        .override(binding.gif.width)
                        .into(binding.gif)
                    return@async
                }

            }
        }
        fetchDBJob.await()
        currPost = postList.size - 1
        show(currPost)
    }

    private fun show(currPost: Int) {
        showProcessing()
        Log.i("curr", currPost.toString())
        if(currPost == 0) {
            binding.previous.imageAlpha = 0x20
        } else {
            binding.previous.imageAlpha = 0xFF
        }
        Glide.with(GlobalApp.instance)
            .load(postList[currPost].url)
            .override(binding.gif.width)
            .placeholder(R.drawable.progress)
            .into(binding.gif)
        binding.description.text = postList[currPost].descr
        binding.processing.visibility = View.GONE
        binding.gif.visibility = View.VISIBLE
        binding.description.visibility = View.VISIBLE
    }

    private fun addToPostList(post: PostCached) {
        postList.add(post)
    }

    private fun updatePostList(list: MutableList<PostCached>){
        postList = list
    }
}


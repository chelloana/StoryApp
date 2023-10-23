package com.example.storydicodingapp.ui.main

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storydicodingapp.R
import com.example.storydicodingapp.data.remote.ApiConfig
import com.example.storydicodingapp.databinding.ActivityMainBinding
import com.example.storydicodingapp.ui.adapters.StoriesAdapter
import com.example.storydicodingapp.ui.detail.DetailActivity
import com.example.storydicodingapp.ui.detail.DetailActivity.Companion.KEY_DETAIL
import com.example.storydicodingapp.ui.splash.SplashActivity
import com.example.storydicodingapp.ui.upload.UploadActivity
import com.example.storydicodingapp.utils.SettingsPreferences
import com.example.storydicodingapp.utils.dataStore

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val savedToken by lazy { intent.getStringExtra(KEY_TOKEN) }
    private val mainViewModel by viewModels<MainViewModel> {
        MainViewModelFactory.getInstance(
            SettingsPreferences.getInstance(dataStore),
            ApiConfig.getApiService(savedToken),
        )
    }

    private val storiesAdapter = StoriesAdapter()

    private val uploadLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            mainViewModel.getStory()
            binding.rvStories.smoothScrollToPosition(0)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeViewModel()
        setListeners()
    }

    private fun observeViewModel() {
        mainViewModel.apply {
            isLoading.observe(this@MainActivity) {
                showLoading(it)
            }

            storyList.observe(this@MainActivity) {
                storiesAdapter.submitList(it)
                binding.rvStories.smoothScrollToPosition(0)
            }

            errorText.observe(this@MainActivity) { event ->
                event.getContentIfNotHandled()?.let { message ->
                    showToast(message)
                }
            }
        }
    }

    private fun setListeners() {
        binding.apply {
            toolbar.apply {
                inflateMenu(R.menu.option_menu)
                setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.menu_refresh -> {
                            mainViewModel.getStory()
                            rvStories.smoothScrollToPosition(0)
                        }

                        R.id.menu_logout -> {
                            val iAuth = Intent(this@MainActivity, SplashActivity::class.java)
                            mainViewModel.clearPrefs()
                            finishAffinity()
                            startActivity(iAuth)
                        }
                    }

                    return@setOnMenuItemClickListener true
                }
            }

            fabAdd.setOnClickListener {
                val iUpload = Intent(this@MainActivity, UploadActivity::class.java)
                iUpload.putExtra(KEY_TOKEN, savedToken)
                uploadLauncher.launch(iUpload)
            }

            rvStories.apply {
                storiesAdapter.onStoryClick = { story, view ->
                    val ivStory = view.findViewById<ImageView>(R.id.iv_story)
                    val detailLayout =
                        view.findViewById<LinearLayout>(R.id.layout_detail)

                    val iDetail = Intent(this@MainActivity, DetailActivity::class.java)
                    iDetail.putExtra(KEY_DETAIL, story)

                    val optionsCompat: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            this@MainActivity,
                            androidx.core.util.Pair(ivStory, "image"),
                            androidx.core.util.Pair(detailLayout, "layoutDetail"),
                        )

                    startActivity(iDetail, optionsCompat.toBundle())
                }

                adapter = storiesAdapter
                layoutManager = LinearLayoutManager(this@MainActivity)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressbar.isVisible = isLoading
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val KEY_TOKEN = "key_token"
    }
}
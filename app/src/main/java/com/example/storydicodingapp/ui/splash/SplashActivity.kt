package com.example.storydicodingapp.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import com.example.storydicodingapp.R
import com.example.storydicodingapp.databinding.ActivitySplashBinding
import com.example.storydicodingapp.ui.login.LoginActivity
import com.example.storydicodingapp.ui.main.MainActivity
import com.example.storydicodingapp.ui.main.MainActivity.Companion.KEY_TOKEN
import com.example.storydicodingapp.utils.SettingsPreferences
import com.example.storydicodingapp.utils.SettingsPreferences.Companion.preferencesDefaultValue
import com.example.storydicodingapp.utils.dataStore

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private val splashViewModel by viewModels<SplashViewModel> {
        SplashViewModelFactory.getInstance(
            SettingsPreferences.getInstance(dataStore)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeViewModel()
    }

    private fun observeViewModel() {
        splashViewModel.getPrefs().observe(this) { token ->
            Handler(Looper.getMainLooper()).postDelayed({
                if (token == preferencesDefaultValue) {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                } else {
                    val iMain = Intent(this, MainActivity::class.java)
                    iMain.putExtra(KEY_TOKEN, token)
                    startActivity(iMain)
                    finish()
                }
            }, 1000L)
        }
    }
}
package com.example.storydicodingapp.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.example.storydicodingapp.R
import com.example.storydicodingapp.data.remote.ApiConfig
import com.example.storydicodingapp.databinding.ActivityLoginBinding
import com.example.storydicodingapp.ui.main.MainActivity
import com.example.storydicodingapp.ui.main.MainActivity.Companion.KEY_TOKEN
import com.example.storydicodingapp.ui.register.RegisterActivity
import com.example.storydicodingapp.utils.Event
import com.example.storydicodingapp.utils.Result
import com.example.storydicodingapp.utils.SettingsPreferences
import com.example.storydicodingapp.utils.dataStore

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel by viewModels<LoginViewModel> {
        LoginViewModelFactory.getInstance(
            SettingsPreferences.getInstance(dataStore),
            ApiConfig.getApiService(null),
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeViewModel()
        setListeners()
    }

    private fun observeViewModel() {
        with(loginViewModel) {
            canLogin.observe(this@LoginActivity) {
                binding.btnLogin.isEnabled = it
            }

            isLoading.observe(this@LoginActivity) {
                showLoading(it)
            }

            errorMessage.observe(this@LoginActivity) { event ->
                event.getContentIfNotHandled()?.let { message ->
                    showToast(message)
                }
            }
        }
    }

    private fun setListeners() {
        with(binding) {
            edEmail.addTextChangedListener(onTextChanged = { email, _, _, _ ->
                edEmail.error =
                    if (!Patterns.EMAIL_ADDRESS.matcher(email.toString())
                            .matches() && !email.isNullOrEmpty()
                    ) getString(R.string.error_email) else null

                loginViewModel.isEmailError.postValue(!edEmail.error.isNullOrEmpty() || edEmail.text.isNullOrEmpty())
            })

            edPassword.addTextChangedListener(onTextChanged = { _, _, _, _ ->
                loginViewModel.isPassError.postValue(!edPassword.error.isNullOrEmpty() || edPassword.text.isNullOrEmpty())
            })

            btnLogin.setOnClickListener {
                loginViewModel.loginUser(edEmail.text.toString(), edPassword.text.toString())
                    .observe(this@LoginActivity) { result ->
                        when (result) {
                            is Result.Loading -> {
                                loginViewModel.isLoading.postValue(true)
                            }

                            is Result.Success -> {
                                loginViewModel.isLoading.postValue(false)
                                loginViewModel.savePrefs(result.data.loginResult.token)
                                observeToken()
                            }

                            is Result.Error -> {
                                loginViewModel.isLoading.postValue(false)
                                loginViewModel.errorMessage.postValue(Event(result.error))
                            }
                        }
                    }
            }

            btnRegister.setOnClickListener {
                val iRegister = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(iRegister)
            }
        }
    }

    private fun observeToken() {
        loginViewModel.getPrefs().observe(this@LoginActivity) { token ->
            if (token != SettingsPreferences.preferencesDefaultValue) {
                val iMain = Intent(this@LoginActivity, MainActivity::class.java)
                iMain.putExtra(KEY_TOKEN, token)
                startActivity(iMain)
                finish()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        with(binding) {
            progressbar.isVisible = isLoading
            btnLogin.isVisible = !isLoading
            edEmail.isEnabled = !isLoading
            edPassword.isEnabled = !isLoading
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
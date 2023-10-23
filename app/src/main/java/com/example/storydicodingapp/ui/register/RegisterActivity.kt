package com.example.storydicodingapp.ui.register

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.example.storydicodingapp.utils.Result
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import com.example.storydicodingapp.R
import com.example.storydicodingapp.data.remote.ApiConfig
import com.example.storydicodingapp.databinding.ActivityRegisterBinding
import com.example.storydicodingapp.utils.Event

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val registerViewModel by viewModels<RegisterViewModel> {
        RegisterViewModelFactory.getInstance(
            ApiConfig.getApiService(null)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeViewModel()
        setListeners()
    }

    private fun observeViewModel() {
        with(registerViewModel) {
            canRegister.observe(this@RegisterActivity) {
                binding.btnRegister.isEnabled = it
            }

            isLoading.observe(this@RegisterActivity) { isLoading ->
                showLoading(isLoading)
            }

            errorText.observe(this@RegisterActivity) { event ->
                event.getContentIfNotHandled()?.let { message ->
                    showToast(message)
                }
            }
        }
    }

    private fun setListeners() {
        binding.apply {
            edUsername.addTextChangedListener(onTextChanged = { _, _, _, _ ->
                registerViewModel.isUsernameError.postValue(edUsername.text.isNullOrEmpty())
            })

            edEmail.addTextChangedListener(onTextChanged = { email, _, _, _ ->
                edEmail.error =
                    if (!Patterns.EMAIL_ADDRESS.matcher(email.toString())
                            .matches() && !email.isNullOrEmpty()
                    ) getString(R.string.error_email) else null
                registerViewModel.isEmailError.postValue(!edEmail.error.isNullOrEmpty() || edEmail.text.isNullOrEmpty())
            })

            edPassword.addTextChangedListener(onTextChanged = { _, _, _, _ ->
                registerViewModel.isPassError.postValue(!edPassword.error.isNullOrEmpty() || edPassword.text.isNullOrEmpty())
            })

            btnLogin.setOnClickListener {
                finish()
            }

            btnRegister.setOnClickListener {
                registerViewModel.register(
                    edUsername.text.toString(),
                    edEmail.text.toString(),
                    edPassword.text.toString()
                )
                    .observe(this@RegisterActivity) { result ->
                        when (result) {
                            is Result.Loading -> {
                                registerViewModel.isLoading.postValue(true)
                            }

                            is Result.Success -> {
                                registerViewModel.isLoading.postValue(false)
                                registerViewModel.errorText.postValue(Event(result.data.message.toString()))
                                finish()
                            }

                            is Result.Error -> {
                                registerViewModel.isLoading.postValue(false)
                                registerViewModel.errorText.postValue(Event(result.error))
                            }

                        }
                    }
            }
        }

    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            progressbar.isVisible = isLoading
            btnRegister.isVisible = !isLoading
            edUsername.isEnabled = !isLoading
            edEmail.isEnabled = !isLoading
            edPassword.isEnabled = !isLoading
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
package com.c22ps049.shovl.ui

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.c22ps049.shovl.R
import com.c22ps049.shovl.databinding.ActivityRegisterBinding
import com.c22ps049.shovl.network.ApiConfig
import com.c22ps049.shovl.network.ApiService
import com.c22ps049.shovl.network.RegisterResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var countDownTimer: CountDownTimer
    private var currentProgress = 0
    private val retrofit: ApiService = ApiConfig.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        registerAction()
    }

    private fun showLoading(state: Boolean) {
        if (state){
            binding.registerProgressbar.visibility = View.VISIBLE
            countDownTimer = object : CountDownTimer(2000, 10) {
                override fun onTick(millisUntilFinished: Long) {
                    currentProgress += 1
                    binding.registerProgressbar.progress = currentProgress
                    binding.registerProgressbar.max = 100
                }

                override fun onFinish() {

                }

            }
            countDownTimer.start()

        }
        else binding.registerProgressbar.visibility = View.GONE
    }

    private fun registerAction() {
        binding.btnSignup.setOnClickListener {
            val username = binding.usernameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            when {
                username.isEmpty() -> {
                    binding.usernameEditTextLayout.error = getString(R.string.name_empty)
                }
                email.isEmpty() -> {
                    binding.emailEditTextLayout.error = getString(R.string.email_empty)
                }
                password.isEmpty() -> {
                    binding.passwordEditTextLayout.error = getString(R.string.empty_password)
                }
                else -> {
                    register(email, password, username)
                }
            }
        }
    }

    private fun register(email: String, password: String, username: String) {
        showLoading(true)
        retrofit.registerUser(createJsonRequestBody("email" to email, "password" to password, "username" to username))
            .enqueue(object : Callback<RegisterResponse> {
                override fun onResponse(
                    call: Call<RegisterResponse>,
                    response: Response<RegisterResponse>
                ) {
                    val responseBody = response.body()
                    showLoading(false)
                    if (response.isSuccessful && responseBody?.message == "User Created") {
                        Toast.makeText(this@RegisterActivity, responseBody.message, Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Log.e("TAG", "onFailure: ${response.message()}")
                        Toast.makeText(this@RegisterActivity, response.message(), Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    Toast.makeText(this@RegisterActivity, getString(R.string.register_failed), Toast.LENGTH_SHORT).show()
                    showLoading(false)
                }

            })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun createJsonRequestBody(vararg params: Pair<String, String>) =
        JSONObject(mapOf(*params)).toString()
            .toRequestBody("application/json; charset=utf-8".toMediaType())

}
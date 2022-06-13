package com.c22ps049.shovl.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.c22ps049.shovl.R
import com.c22ps049.shovl.SharedViewModel
import com.c22ps049.shovl.ViewModelFactory
import com.c22ps049.shovl.database.UserPreferences
import com.c22ps049.shovl.databinding.ActivityLoginBinding
import com.c22ps049.shovl.network.ApiConfig
import com.c22ps049.shovl.network.ApiService
import com.c22ps049.shovl.network.LoginResponse
import com.c22ps049.shovl.network.UserModel
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel : SharedViewModel
    private lateinit var countDownTimer: CountDownTimer
    private var currentProgress = 0
    private val retrofit: ApiService = ApiConfig.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loginAction()
        setupViewModel()

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun setupViewModel() {
        loginViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreferences.getInstance(dataStore), this)
        )[SharedViewModel::class.java]
    }

    private fun loginAction(){
        binding.btnSignin.setOnClickListener{
            val username = binding.usernameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            when {
                username.isEmpty() -> {
                    binding.usernameEditTextLayout.error = getString(R.string.name_empty)
                }
                password.isEmpty() -> {
                    binding.passwordEditTextLayout.error = getString(R.string.empty_password)
                }
                else -> {
                    login(username, password)
                }
            }
        }
    }
    private fun showLoading(state: Boolean) {

        if (state){
            binding.loginProgressbar.visibility = View.VISIBLE
            countDownTimer = object : CountDownTimer(2000, 10) {
                override fun onTick(millisUntilFinished: Long) {
                    currentProgress += 1
                    binding.loginProgressbar.progress = currentProgress
                    binding.loginProgressbar.max = 100
                }

                override fun onFinish() {

                }

            }
            countDownTimer.start()

        }
        else binding.loginProgressbar.visibility = View.GONE
    }

    private fun login(username: String, password: String){
        showLoading(true)
        retrofit.loginUser(createJsonRequestBody("username" to username, "password" to password)).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                val responseBody = response.body()
                showLoading(false)
                if (response.isSuccessful && responseBody?.message == "success") {
                    loginViewModel.saveUser(UserModel(responseBody.loginResult.userId,responseBody.loginResult.name, responseBody.loginResult.token, true))
                    Toast.makeText(this@LoginActivity, "Login Successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@LoginActivity, ProfileActivity::class.java)
                    startActivity(intent)
                    finish()

                } else {
                    Log.e("TAG", "onFailure: ${responseBody?.message}")
                    Toast.makeText(this@LoginActivity, "Invalid credentials", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Failed to Login", Toast.LENGTH_SHORT).show()
                showLoading(false)
            }

        })
    }

    private fun createJsonRequestBody(vararg params: Pair<String, String>) =
        JSONObject(mapOf(*params)).toString()
            .toRequestBody("application/json; charset=utf-8".toMediaType())

//    private fun jsonLogin(username: String, password: String){
//        try {
//            val paramObject: JSONObject = JSONObject()
//            paramObject.put(username, password)
//
//            retrofit.jsonLogin(paramObject.toString()).enqueue(object : Callback<LoginResponse> {
//                override fun onResponse(
//                    call: Call<LoginResponse>,
//                    response: Response<LoginResponse>
//                ) {
//                    TODO("Not yet implemented")
//                }
//
//                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
//                    TODO("Not yet implemented")
//                }
//
//            })
//        } catch ()
//    }
}
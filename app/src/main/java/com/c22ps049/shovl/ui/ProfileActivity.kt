package com.c22ps049.shovl.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.c22ps049.shovl.SharedViewModel
import com.c22ps049.shovl.ViewModelFactory
import com.c22ps049.shovl.database.UserPreferences
import com.c22ps049.shovl.databinding.ActivityProfileBinding
import com.c22ps049.shovl.network.ApiConfig
import com.c22ps049.shovl.network.ApiService
import com.c22ps049.shovl.network.GetUserResponse
import com.c22ps049.shovl.network.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var profileViewModel: SharedViewModel
    
    private lateinit var countDownTimer: CountDownTimer
    private var currentProgress = 0

    private val retrofit: ApiService = ApiConfig.create()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)

        setContentView(binding.root)

        setupViewModel()
        getUserData()

        profileViewModel.getUser().observe(this) {user ->
            if (user.isLogin) {
                Log.d("TAG", "Login Success")
            } else {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        binding.btnSignout.setOnClickListener {
            profileViewModel.logout()
        }
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun showLoading(state: Boolean) {

        if (state){
            binding.profileProgressbar.visibility = View.VISIBLE
            countDownTimer = object : CountDownTimer(2000, 10) {
                override fun onTick(millisUntilFinished: Long) {
                    currentProgress += 1
                    binding.profileProgressbar.progress = currentProgress
                    binding.profileProgressbar.max = 100
                }
                override fun onFinish() {

                }

            }
            countDownTimer.start()

        }
        else binding.profileProgressbar.visibility = View.GONE
    }

    private fun setupViewModel() {
        profileViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreferences.getInstance(dataStore), this)
        )[SharedViewModel::class.java]
    }

    private fun getUserData() {
        showLoading(true)
        profileViewModel.getUser().observe(this) { user ->
            val token = user.token
            retrofit.getUserData("Bearer $token", user.userId).enqueue(object : Callback<GetUserResponse> {
                override fun onResponse(
                    call: Call<GetUserResponse>,
                    response: Response<GetUserResponse>
                ) {
                    val responseBody = response.body()
                    showLoading(false)
                    if (response.isSuccessful && responseBody?.message == "success") {
                        profileViewModel.saveUserEmail(User(responseBody.user.email, responseBody.user.username))
                    } else Log.e("TAG", "onFailure: ${responseBody?.message}")
                }

                override fun onFailure(call: Call<GetUserResponse>, t: Throwable) {
                    showLoading(false)
                }

            })
        }
        profileViewModel.getUserData().observe(this) { data ->
            binding.tvUsername.text = data.username
            binding.tvEmail.text = data.email
        }
    }


}
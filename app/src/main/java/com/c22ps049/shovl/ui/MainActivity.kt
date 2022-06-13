package com.c22ps049.shovl.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.c22ps049.shovl.R
import com.c22ps049.shovl.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGetStarted.setOnClickListener(this)
        binding.btnLogin.setOnClickListener(this)

        setupView()

        playAnimation()
    }

    override fun onClick(p0: View?) {
        when (p0?.id){
            R.id.btn_get_started -> {
                val toRegisterIntent = Intent(this@MainActivity, RegisterActivity::class.java)
                startActivity(toRegisterIntent)
            }
            R.id.btn_login ->{
                val loginIntent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(loginIntent)
            }
        }
    }
    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }


    private fun playAnimation() {
        val appName = ObjectAnimator.ofFloat(binding.titleName, View.ALPHA, 1f).setDuration(1000)
        val appLogo = ObjectAnimator.ofFloat(binding.titleLogo, View.ALPHA, 1f).setDuration(1000)

        AnimatorSet().apply {
            playSequentially(appName, appLogo)
            start()
        }
    }
}
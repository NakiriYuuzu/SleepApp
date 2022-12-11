package com.example.sleep

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.sleep.databinding.ActivityLoginBinding
import com.example.sleep.library.DialogHelper
import com.example.sleep.library.ShareHelper
import com.example.sleep.library.ViewHelper
import com.example.sleep.util.Constant

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var shareHelper: ShareHelper
    private lateinit var dialogHelper: DialogHelper

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initViewModel()
        initButton()
        // startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        // finish()
    }

    private fun initViewModel() {
        lifecycleScope.launchWhenStarted {
            viewModel.state.collect {
                when (it) {
                    is LoginViewModel.LoginState.Success -> {
                        dialogHelper.hideLoading()
                        val account = binding.loginInputAcc.text.toString()
                        if (account == "admin") shareHelper.put("admin", Constant.GRANT)
                        else shareHelper.put("user", Constant.GRANT)

                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    }

                    is LoginViewModel.LoginState.Error -> {
                        dialogHelper.hideLoading()
                        dialogHelper.showDialog("錯誤", it.message, false)
                    }

                    is LoginViewModel.LoginState.Loading -> {
                        dialogHelper.showLoading()
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun initButton() {
        binding.loginBtnSignIn.setOnClickListener {
            viewModel.login(binding.loginInputAcc.text.toString(), binding.loginInputPass.text.toString())
        }
    }

    private fun initView() {
        ViewHelper(this).setupUI(binding.root)
        shareHelper = ShareHelper(this)
        dialogHelper = DialogHelper(this)

        binding.loginAnimation.setAnimation(R.raw.login2)
        binding.loginAnimation.playAnimation()
    }
}
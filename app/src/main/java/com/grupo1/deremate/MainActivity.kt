package com.grupo1.deremate

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.color.DynamicColors
import com.grupo1.deremate.databinding.ActivityMainBinding
import com.grupo1.deremate.repository.UserRepository
import com.grupo1.deremate.session.SessionManager
import com.grupo1.deremate.util.BiometricHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject lateinit var sessionManager: SessionManager
    @Inject lateinit var userRepository: UserRepository

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        DynamicColors.applyToActivityIfAvailable(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val token = sessionManager.getToken()
        if (token != null) {
            val biometricHelper = BiometricHelper(
                activity = this,
                onSuccess = { navigateWithTokenValidation() },
                onFailure = { navigateToLogin() }
            )

            if (biometricHelper.isBiometricAvailable()) {
                biometricHelper.showBiometricPrompt()
            } else {
                navigateWithTokenValidation()
            }
        } else {
            navigateToLogin()
        }
    }

    private fun navigateWithTokenValidation() {
        sessionManager.isValidToken { isValid ->
            runOnUiThread {
                if (isValid) {
                    startActivity(Intent(this, DashboardActivity::class.java))
                } else {
                    navigateToLogin()
                }
                finish()
            }
        }
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}

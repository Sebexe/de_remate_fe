package com.grupo1.deremate

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.color.DynamicColors
import com.grupo1.deremate.apis.AuthControllerApi
import com.grupo1.deremate.databinding.ActivityMainBinding
import com.grupo1.deremate.databinding.ActivityRegisterBinding
import com.grupo1.deremate.infrastructure.ApiClient
import com.grupo1.deremate.models.GenericResponseDTOObject
import com.grupo1.deremate.models.GenericResponseDTOString
import com.grupo1.deremate.models.LoginRequestDTO
import com.grupo1.deremate.models.LoginResponseDTO
import com.grupo1.deremate.util.parseErrorBody
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {


    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        DynamicColors.applyToActivityIfAvailable(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupLoginBtn()
    }

    fun setupLoginBtn() {
        binding.btnIngresar.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}


package com.grupo1.deremate

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.color.DynamicColors
import com.grupo1.deremate.databinding.ActivityMainBinding
import com.grupo1.deremate.repository.UserRepository
import com.grupo1.deremate.session.SessionManager
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
/*
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        binding.filledButtonSignUp.setOnClickListener{
            println("Pulsamos el boton");
            val intent = Intent (this,RegisterActivity::class.java)
            startActivity(intent)
        }
        
        val apiClient = ApiClient("http://10.0.2.2:8080") // 👈 IP especial para emulador Android
        val authApi = apiClient.createService(AuthControllerApi::class.java)

        val request = LoginRequestDTO("sebyex18@gmail.com","holaMundo!2")

        authApi.login(request).enqueue(object : Callback<GenericResponseDTOObject> {
            override fun onResponse(
                call: Call<GenericResponseDTOObject>,

                response: Response<GenericResponseDTOObject>
            ) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()

                } else {
                    val error = parseErrorBody<GenericResponseDTOString>(response.errorBody())
                    println(error)
                    Toast.makeText(
                        this@MainActivity,
                        "Login fallido: ${error?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }



            override fun onFailure(call: Call<GenericResponseDTOObject>, t: Throwable) {
                Toast.makeText(
                    this@MainActivity,
                    "Error de red: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
         })
    */
        sessionManager.isValidToken { isValid ->
            if (isValid) {
                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)
                finish()
            } else if (userRepository.user != null && (userRepository.user.isEmailVerified == null || !userRepository.user.isEmailVerified!!)) {
                val intent = Intent(this, VerifyEmail::class.java)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}

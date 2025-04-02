package com.grupo1.deremate

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.color.DynamicColors
import com.grupo1.deremate.apis.AuthControllerApi
import com.grupo1.deremate.databinding.ActivityMainBinding
import com.grupo1.deremate.infrastructure.ApiClient
import com.grupo1.deremate.models.GenericResponseDTOObject
import com.grupo1.deremate.models.GenericResponseDTOString
import com.grupo1.deremate.models.LoginRequestDTO
import com.grupo1.deremate.models.LoginResponseDTO
import com.grupo1.deremate.util.parseErrorBody
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response

class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        DynamicColors.applyToActivityIfAvailable(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
    }


    }

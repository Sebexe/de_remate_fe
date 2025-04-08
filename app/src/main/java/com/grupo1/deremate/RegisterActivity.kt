package com.grupo1.deremate

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.color.DynamicColors
import com.grupo1.deremate.apis.AuthControllerApi
import com.grupo1.deremate.databinding.ActivityRegisterBinding
import com.grupo1.deremate.infrastructure.ApiClient
import com.grupo1.deremate.models.SignupRequestDTO
import com.grupo1.deremate.models.GenericResponseDTOString
import com.grupo1.deremate.models.UserDTO
import com.grupo1.deremate.repository.UserRepository
import com.grupo1.deremate.util.parseErrorBody
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    @Inject
    lateinit var apiClient: ApiClient

    @Inject
    lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        DynamicColors.applyToActivityIfAvailable(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupLoginBtn()
        setupRegisterBtn()
    }

    private fun setupLoginBtn() {
        binding.btnIngresar.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun setupRegisterBtn() {
        binding.btnRegistrarse.setOnClickListener {
            val nombre = binding.etNombre.text.toString().trim()
            val apellido = binding.etApellido.text.toString().trim()
            val correo = binding.etCorreo.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (nombre.isEmpty() || apellido.isEmpty() || correo.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, completá todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val signupDto = SignupRequestDTO(
                firstName = nombre,
                lastName = apellido,
                email = correo,
                password = password
            )

            val authApi = apiClient.createService(AuthControllerApi::class.java)
            authApi.signup(signupDto).enqueue(object : Callback<GenericResponseDTOString> {
                override fun onResponse(
                    call: Call<GenericResponseDTOString>,
                    response: Response<GenericResponseDTOString>
                ) {
                    if (response.isSuccessful) {
                        var userDTO = UserDTO(email = correo);
                        userRepository.saveUser(userDTO)

                        Toast.makeText(this@RegisterActivity, "Registro exitoso", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@RegisterActivity, VerifyEmail::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        val errorMsg = "No podemos registrar el email $correo"
                        Toast.makeText(this@RegisterActivity, errorMsg, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<GenericResponseDTOString>, t: Throwable) {
                    Toast.makeText(this@RegisterActivity, "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}

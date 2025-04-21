package com.grupo1.deremate

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.color.DynamicColors
import com.grupo1.deremate.apis.AuthControllerApi
import com.grupo1.deremate.databinding.ActivityRegisterBinding
import com.grupo1.deremate.infrastructure.ApiClient
import com.grupo1.deremate.models.GenericResponseDTOString
import com.grupo1.deremate.models.SignupRequestDTO
import com.grupo1.deremate.models.UserDTO
import com.grupo1.deremate.repository.UserRepository
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {


    private val TAG = "RegisterActivity"
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
            finish() // Cierra esta actividad al ir a Login
        }
    }

    private fun setupRegisterBtn() {
        binding.btnRegistrarse.setOnClickListener {
            // Resetear errores previos
            binding.etNombre.error = null
            binding.etApellido.error = null
            binding.etCorreo.error = null
            binding.etPassword.error = null

            // Obtener valores usando .text.toString().trim()
            val nombre = binding.etNombre.text.toString().trim()
            val apellido = binding.etApellido.text.toString().trim()
            val correo = binding.etCorreo.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            // --- VALIDACIONES FRONTEND ---
            var isValid = true

            // Validación Nombre
            if (nombre.isBlank()) { // isNullOrBlank es más idiomático en Kotlin
                binding.etNombre.error = getString(R.string.validation_name_required) // Usar strings.xml es mejor
                isValid = false
            }
            else if (!nombre.matches(Regex("^[\\p{L} .'-]+$"))) { // Validación regex opcional
                 binding.etNombre.error = getString(R.string.validation_name_invalid)
                 isValid = false
             }

            // Validación Apellido
            if (apellido.isBlank()) {
                binding.etApellido.error = getString(R.string.validation_lastname_required)
                isValid = false
            }
             else if (!apellido.matches(Regex("^[\\p{L} .'-]+$"))) { // Validación regex opcional
                 binding.etApellido.error = getString(R.string.validation_lastname_invalid)
                 isValid = false
             }

            // Validación Correo
            if (correo.isBlank()) {
                binding.etCorreo.error = getString(R.string.validation_email_required)
                isValid = false
            } else if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                binding.etCorreo.error = getString(R.string.validation_email_invalid)
                isValid = false
            }

            // Validación Contraseña (solo si está vacía)
            if (password.isBlank()) {
                binding.etPassword.error = getString(R.string.validation_password_required)
                isValid = false
            }
             else if (password.length < 6) { // Validación opcional de longitud
                 binding.etPassword.error = getString(R.string.validation_password_length)
                 isValid = false
             }


            if (!isValid) {
                return@setOnClickListener
            }
            // --- FIN VALIDACIONES FRONTEND ---


            val signupDto = SignupRequestDTO(
                firstName = nombre,
                lastName = apellido,
                email = correo,
                password = password
            )

            Log.d(TAG, "Attempting signup with DTO: $signupDto") // Log antes de llamar

            val authApi = apiClient.createService(AuthControllerApi::class.java)


            authApi.signup(signupDto).enqueue(object : Callback<GenericResponseDTOString> {
                override fun onResponse(
                    call: Call<GenericResponseDTOString>,
                    response: Response<GenericResponseDTOString>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        // Éxito
                        val successMsg = response.body()?.data ?: "Registro exitoso, verifica tu email"
                        Log.i(TAG, "Signup successful for email: $correo. Response: $successMsg")

                        val userToSave = UserDTO(email = correo)
                        try {
                            userRepository.saveUser(userToSave)
                            Log.d(TAG, "User email ($correo) saved to UserRepository for VerifyEmail compatibility.")
                        } catch (e: Exception) {

                            Log.e(TAG, "Error saving user to UserRepository", e)

                        }


                        Toast.makeText(this@RegisterActivity, successMsg, Toast.LENGTH_LONG).show()
                        val intent = Intent(this@RegisterActivity, VerifyEmail::class.java).apply {
                            putExtra("USER_EMAIL", correo)
                        }
                        startActivity(intent)
                        finish()
                    } else {
                        // --- MANEJO DE ERROR DEL BACKEND ---
                        val errorMsg = parseErrorMessage(response.errorBody()) // Usar el parser
                        Log.e(TAG, "Signup failed: Code=${response.code()}, Message=${errorMsg}")
                        Toast.makeText(this@RegisterActivity, errorMsg, Toast.LENGTH_LONG).show() // Mostrar error parseado
                        // --- FIN MANEJO DE ERROR ---
                    }
                }

                override fun onFailure(call: Call<GenericResponseDTOString>, t: Throwable) {
                    // Error de Red
                    Log.e(TAG, "Signup network failure", t)
                    Toast.makeText(this@RegisterActivity, "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }


    private fun parseErrorMessage(errorBody: ResponseBody?): String {
        if (errorBody == null) {
            Log.w("ParseError", "Error body was null.")

            return "Error desconocido (sin respuesta)"
        }

        return try {
            val errorBodyString = errorBody.string() // Leer el cuerpo del error
            Log.d("ParseError", "Raw error body: $errorBodyString") // Loguear para depurar
            val jsonObject = JSONObject(errorBodyString)

            // --- PASO 1: Buscar errores específicos en el objeto "data" ---
            if (jsonObject.has("data")) {
                try {
                    val dataObject = jsonObject.getJSONObject("data")
                    val keys = dataObject.keys() // Obtener los nombres de los campos con error (ej: "password")
                    if (keys.hasNext()) {
                        val firstFieldName = keys.next() // Tomar el primer campo con error
                        val specificErrorMessage = dataObject.getString(firstFieldName)
                        Log.d("ParseError", "Found specific error in 'data' for field '$firstFieldName': $specificErrorMessage")

                        return specificErrorMessage
                    }
                } catch (e: JSONException) {

                    Log.w("ParseError", "'data' field found but was not a valid JSONObject: ${e.message}")
                }
            }

            // --- PASO 2: Si no hay errores en "data", buscar "message" global ---
            if (jsonObject.has("message")) {
                val globalMessage = jsonObject.getString("message")
                Log.d("ParseError", "Found global 'message': $globalMessage")
                return globalMessage // Devolver "Validation Failed" o similar
            }


            Log.w("ParseError", "Could not find specific errors in 'data' or a global 'message' key.")

            return "Error en el registro. Intente nuevamente."

        } catch (e: JSONException) {
            Log.e("ParseError", "Error parsing JSON error response: ${e.message}")

            return "Respuesta de error inválida del servidor."
        } catch (e: Exception) {
            Log.e("ParseError", "Error reading error body: ${e.message}")

            return "No se pudo leer la respuesta de error."
        }
    }
}
package com.grupo1.deremate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.grupo1.deremate.apis.AuthControllerApi;
import com.grupo1.deremate.apis.UserControllerApi;
import com.grupo1.deremate.databinding.ActivityLoginBinding;
import com.grupo1.deremate.infrastructure.ApiClient;
import com.grupo1.deremate.models.GenericResponseDTOObject;
import com.grupo1.deremate.models.LoginRequestDTO;
import com.grupo1.deremate.models.UserDTO;
import com.grupo1.deremate.repository.TokenRepository;
import com.grupo1.deremate.repository.UserRepository;

import org.json.JSONObject;

import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;

    @Inject
    ApiClient apiClient;

    @Inject
    TokenRepository tokenRepository;

    @Inject
    UserRepository userRepository;

    AuthControllerApi authControllerApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        apiClient.setToken(""); // limpiar token anterior
        authControllerApi = apiClient.createService(AuthControllerApi.class);

        setupLoginBtn();
        setupRegisterBtn();
    }

    private void setupLoginBtn() {
        binding.btnIniciarSesion.setOnClickListener(view -> callLoginApi());
    }

    private void setupRegisterBtn() {
        binding.btnRegister.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void callLoginApi() {
        String email = binding.etCorreo.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        LoginRequestDTO requestDTO = new LoginRequestDTO(email, password);
        Call<GenericResponseDTOObject> loginCall = authControllerApi.login(requestDTO);

        loginCall.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<GenericResponseDTOObject> call, Response<GenericResponseDTOObject> response) {
                if (!response.isSuccessful()) {
                    String errorMsg = "Error al iniciar sesión";
                    if (response.code() == 401 || response.code() == 404) {
                        errorMsg = parseErrorMessage(response.errorBody());
                    }
                    Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (response.body() == null || response.body().getData() == null) {
                    Toast.makeText(LoginActivity.this, "Respuesta inválida del servidor", Toast.LENGTH_SHORT).show();
                    return;
                }

                Map<String, String> result = (Map<String, String>) response.body().getData();
                String token = result.get("token");

                if (token == null || token.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Token inválido", Toast.LENGTH_SHORT).show();
                    return;
                }

                tokenRepository.saveToken(token);
                apiClient.setToken(token);

                // Obtener datos del usuario
                UserControllerApi userControllerApi = apiClient.createService(UserControllerApi.class);
                userControllerApi.getUserInfo().enqueue(new Callback<>() {
                    @Override
                    public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                        if (response.body() != null) {
                            userRepository.saveUser(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<UserDTO> call, Throwable t) {
                        tokenRepository.clearToken();
                        Log.e("LoginError", "Error al obtener usuario", t);
                    }
                });

                // Ir al dashboard
                Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Call<GenericResponseDTOObject> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String parseErrorMessage(ResponseBody errorBody) {
        try {
            JSONObject json = new JSONObject(errorBody.string());
            return json.optString("message", "Error inesperado");
        } catch (Exception e) {
            Log.e("ParseError", "No se pudo parsear el error", e);
            return "Error desconocido del servidor";
        }
    }
}

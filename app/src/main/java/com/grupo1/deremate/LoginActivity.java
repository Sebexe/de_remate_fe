package com.grupo1.deremate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.grupo1.deremate.apis.AuthControllerApi;
import com.grupo1.deremate.databinding.ActivityLoginBinding;
import com.grupo1.deremate.infrastructure.ApiClient;
import com.grupo1.deremate.models.GenericResponseDTOObject;
import com.grupo1.deremate.models.LoginRequestDTO;
import com.grupo1.deremate.repository.TokenRepository;

import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
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

    AuthControllerApi authControllerApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        apiClient.setToken(""); // si todavía no tenés un token

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
        String email = binding.etCorreo.getText().toString();
        String password = binding.etPassword.getText().toString();

        if (email.isEmpty() || password.isEmpty()) return;

        LoginRequestDTO requestDTO = new LoginRequestDTO(email, password);
        Call<GenericResponseDTOObject> loginCall = authControllerApi.login(requestDTO);

        loginCall.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<GenericResponseDTOObject> call, Response<GenericResponseDTOObject> response) {
                if (response.code() != 200 || response.body() == null) return;

                Map<String, String> result = (Map<String, String>) response.body().getData();
                String token = result.get("token");

                tokenRepository.saveToken(token);
                apiClient.setToken(token); // actualizar token si es necesario en esta instancia

                Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Call<GenericResponseDTOObject> call, Throwable t) {}
        });
    }
}

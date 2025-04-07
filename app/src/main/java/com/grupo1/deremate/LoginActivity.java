package com.grupo1.deremate;

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

    ApiClient apiClient = new ApiClient("http://10.0.2.2:8080", "");
    AuthControllerApi authControllerApi = apiClient.createService(AuthControllerApi.class);

    @Inject TokenRepository tokenRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupLoginBtn();
    }

    private void setupLoginBtn() {
        binding.btnIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callLoginApi();
            }
        });
    }

    private void callLoginApi() {
        String email = binding.etCorreo.getText().toString();
        String password = binding.etPassword.getText().toString();

        Log.d("LOGIN_ACTIVITY", "callLoginApi: " + email + " " + password);

        if (email.isEmpty() || password.isEmpty()) {
            return;
        }

        LoginRequestDTO requestDTO = new LoginRequestDTO(email, password);
        Call<GenericResponseDTOObject> loginCall = authControllerApi.login(requestDTO);
        loginCall.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<GenericResponseDTOObject> call, Response<GenericResponseDTOObject> response) {
                if (response.code() != 200) return;
                assert response.body() != null;
                Log.d("LOGIN_API_RESPONSE", "onResponse: " + response.body());
                Map<String, String> result = (Map<String, String>) response.body().getData();
                String token = result.get("token");
                tokenRepository.saveToken(token);
            }

            @Override
            public void onFailure(Call<GenericResponseDTOObject> call, Throwable t) {

            }
        });
    }
}
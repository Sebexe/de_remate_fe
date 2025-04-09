package com.grupo1.deremate;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.grupo1.deremate.apis.AuthControllerApi;
import com.grupo1.deremate.infrastructure.ApiClient;
import com.grupo1.deremate.models.GenericResponseDTOString;
import com.grupo1.deremate.repository.UserRepository;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class VerifyEmail extends AppCompatActivity {

    @Inject
    ApiClient apiClient;

    @Inject
    UserRepository userRepository;

    private EditText[] codeInputs;

    private AuthControllerApi authControllerApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);
        setupCloseSessionBtn();

        codeInputs = new EditText[]{
                findViewById(R.id.code1),
                findViewById(R.id.code2),
                findViewById(R.id.code3),
                findViewById(R.id.code4)
        };

        for (int i = 0; i < codeInputs.length; i++) {
            int nextIndex = i + 1;
            codeInputs[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 1 && nextIndex < codeInputs.length) {
                        codeInputs[nextIndex].requestFocus();
                    } else if (allFieldsFilled()) {
                        verifyCode();
                    }
                }
            });

            int finalI = i;
            codeInputs[i].setOnKeyListener((v, keyCode, event) -> {
                if (keyCode == android.view.KeyEvent.KEYCODE_DEL
                        && event.getAction() == android.view.KeyEvent.ACTION_DOWN) {
                    if (codeInputs[finalI].getText().toString().isEmpty() && finalI > 0) {
                        codeInputs[finalI - 1].requestFocus();
                        codeInputs[finalI - 1].setText(""); // limpiás el campo anterior también
                        return true;
                    }
                }
                return false;
            });

        }

        findViewById(R.id.btnResend).setOnClickListener(v -> {
            resendCode();
        });
    }

    private void setupCloseSessionBtn() {
        findViewById(R.id.btnClose).setOnClickListener(v -> {
            userRepository.clearUser();
        });
    }

    private boolean allFieldsFilled() {
        for (EditText input : codeInputs) {
            if (input.getText().toString().trim().isEmpty()) return false;
        }
        return true;
    }

    private void verifyCode() {
        StringBuilder tokenBuilder = new StringBuilder();
        for (EditText input : codeInputs) {
            tokenBuilder.append(input.getText().toString().trim());
        }

        String token = tokenBuilder.toString();

        authControllerApi = apiClient.createService(AuthControllerApi.class);
        authControllerApi.verifyEmail(token, userRepository.getUser().getEmail()).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<GenericResponseDTOString> call, Response<GenericResponseDTOString> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(VerifyEmail.this, "Correo verificado correctamente", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(VerifyEmail.this, LoginActivity.class));
                    finish();
                } else {
                    Toast.makeText(VerifyEmail.this, "Código incorrecto", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponseDTOString> call, Throwable t) {
                Toast.makeText(VerifyEmail.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resendCode() {
        authControllerApi = apiClient.createService(AuthControllerApi.class);
        authControllerApi.resendVerification(userRepository.getUser().getEmail()).enqueue(new Callback<GenericResponseDTOString>() {
            @Override
            public void onResponse(Call<GenericResponseDTOString> call, Response<GenericResponseDTOString> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(VerifyEmail.this, "Se ha enviado un nuevo código de verificación", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(VerifyEmail.this, "Ups, algo falló, volvé a intentar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GenericResponseDTOString> call, Throwable t) {
                Toast.makeText(VerifyEmail.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

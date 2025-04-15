package com.grupo1.deremate.session;

import android.util.Log; // Import Log

import com.grupo1.deremate.apis.UserControllerApi;
import com.grupo1.deremate.callback.TokenValidationCallback;
import com.grupo1.deremate.infrastructure.ApiClient;
import com.grupo1.deremate.models.UserDTO;
import com.grupo1.deremate.repository.TokenRepository;
import com.grupo1.deremate.repository.UserRepository;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class SessionManagerImpl implements SessionManager {

    private static final String TAG = "SessionManagerImpl"; // Tag para logs

    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    @Inject
    ApiClient apiClient;

    @Inject
    public SessionManagerImpl(TokenRepository tokenRepository, ApiClient apiClient, UserRepository userRepository) {
        this.tokenRepository = tokenRepository;
        this.apiClient = apiClient;
        this.userRepository = userRepository;
    }

    @Override
    public void isValidToken(TokenValidationCallback callback) {
        String token = tokenRepository.getToken();

        if (token == null || token.isEmpty()) {
            Log.w(TAG, "isValidToken: Token is null or empty. Validation failed.");
            callback.onResult(false);
            return;
        }


        Long userIdFromToken = tokenRepository.getUserIdFromToken();
        if (userIdFromToken != null) {
            Log.d(TAG, "isValidToken: User ID extracted directly from token: " + userIdFromToken);

        } else {
            Log.w(TAG, "isValidToken: Could not extract User ID directly from token (token might be invalid or lack claim).");

        }



        Log.d(TAG, "isValidToken: Proceeding with API validation for token.");
        apiClient.setToken(token);
        UserControllerApi userControllerApi = apiClient.createService(UserControllerApi.class);

        userControllerApi.getUserInfo().enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "isValidToken - onResponse: API validation successful (Code: " + response.code() + ")");
                    userRepository.saveUser(response.body());
                    callback.onResult(true);
                } else {
                    Log.w(TAG, "isValidToken - onResponse: API validation failed or empty body (Code: " + response.code() + ")");
                    resetSession(); // Limpiar sesión si la validación falla
                    callback.onResult(false);
                }
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                Log.e(TAG, "isValidToken - onFailure: API call failed.", t);
                resetSession(); // Limpiar sesión en caso de fallo de red/API
                callback.onResult(false);
            }
        });
    }

    @Override
    public String getToken() {

        return tokenRepository.getToken();
    }

    private void resetSession() {
        Log.w(TAG, "Resetting session: Clearing user and token data.");
        userRepository.clearUser();
        tokenRepository.clearToken();

    }

    public Long getUserId() {

        return tokenRepository.getUserIdFromToken();
    }
}
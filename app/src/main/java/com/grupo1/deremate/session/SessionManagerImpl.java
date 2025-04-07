package com.grupo1.deremate.session;

import android.util.Log;

import com.grupo1.deremate.apis.UserControllerApi;
import com.grupo1.deremate.callback.TokenValidationCallback;
import com.grupo1.deremate.infrastructure.ApiClient;
import com.grupo1.deremate.models.UserDTO;
import com.grupo1.deremate.repository.TokenRepository;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class SessionManagerImpl implements SessionManager {
    private final TokenRepository tokenRepository;

    private ApiClient apiClient = new ApiClient("http://10.0.2.2:8080", "");

    private UserControllerApi userControllerApi = apiClient.createService(UserControllerApi.class);

    @Inject
    public SessionManagerImpl(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public void isValidToken(TokenValidationCallback callback) {
        String token = tokenRepository.getToken();

        if (token == null || token.isEmpty()) {
            callback.onResult(false);
            return;
        }

        ApiClient apiClient = new ApiClient("http://10.0.2.2:8080", token);
        UserControllerApi userControllerApi = apiClient.createService(UserControllerApi.class);

        userControllerApi.getUserInfo().enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                callback.onResult(response.code() == 200);
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                callback.onResult(false);
            }
        });
    }
}

package com.grupo1.deremate.session;

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

    private final TokenRepository tokenRepository;

    private final UserRepository userRepository;
    private final ApiClient apiClient;

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
            callback.onResult(false);
            return;
        }

        apiClient.setToken(token); // seteás dinámicamente el token actual
        UserControllerApi userControllerApi = apiClient.createService(UserControllerApi.class);

        userControllerApi.getUserInfo().enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                if (response.code() != 200) {
                    resetSession();
                    return;
                }

                userRepository.saveUser(response.body());
                callback.onResult(response.code() == 200);
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                resetSession();
                callback.onResult(false);
            }
        });
    }

    @Override
    public String getToken() {
        return tokenRepository.getToken();
    }

    private void resetSession() {
        userRepository.clearUser();
        tokenRepository.clearToken();
    }
}

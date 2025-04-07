package com.grupo1.deremate.session;

import android.util.Log;

import com.grupo1.deremate.apis.UserControllerApi;
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

    private final UserControllerApi userControllerApi = apiClient.createService(UserControllerApi.class);

    @Inject
    public SessionManagerImpl(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public boolean isValidToken() {
        if (tokenRepository.getToken() == null ||
                tokenRepository.getToken().isEmpty()) {
            return false;
        }

        final boolean[] result = {false};
        Call<UserDTO> userDTOCall = userControllerApi.getUserInfo();

        userDTOCall.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                Log.d("Response","Response: "+response.toString());
                result[0] = true;
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                result[0] = false;
            }
        });

        return result[0];
    }
}

package com.grupo1.deremate.session;

import com.grupo1.deremate.repository.TokenRepository;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SessionManagerImpl implements SessionManager {
    TokenRepository tokenRepository;

    @Inject
    public SessionManagerImpl(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public boolean isValidToken() {
        if (tokenRepository.getToken() == null ||
                tokenRepository.getToken().isEmpty()) {
            return false;
        }

        // validar que el token sea válido pegandole al endpoint de info con el token

        return false;
    }
}

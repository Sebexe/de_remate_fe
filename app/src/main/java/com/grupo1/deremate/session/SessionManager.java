package com.grupo1.deremate.session;

import com.grupo1.deremate.callback.TokenValidationCallback;

import javax.inject.Inject;

public interface SessionManager {
    void isValidToken(TokenValidationCallback tokenValidationCallback);
}

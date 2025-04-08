package com.grupo1.deremate.repository.impl;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.grupo1.deremate.models.UserDTO;
import com.grupo1.deremate.repository.UserRepository;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UserRepositoryImpl implements UserRepository {

    private static final String PREFS_NAME = "user_prefs";
    private static final String KEY_USER = "user";

    private final SharedPreferences sharedPreferences;
    private final Gson gson = new Gson();

    @Inject
    public UserRepositoryImpl(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public void saveUser(UserDTO user) {
        String json = gson.toJson(user);
        sharedPreferences.edit().putString(KEY_USER, json).apply();
    }

    @Override
    public UserDTO getUser() {
        String json = sharedPreferences.getString(KEY_USER, null);
        return json != null ? gson.fromJson(json, UserDTO.class) : null;
    }

    @Override
    public void clearUser() {
        sharedPreferences.edit().remove(KEY_USER).apply();
    }
}

package com.grupo1.deremate.di;

import android.content.Context;

import com.grupo1.deremate.repository.UserRepository;
import com.grupo1.deremate.repository.impl.UserRepositoryImpl;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

import javax.inject.Singleton;

@Module
@InstallIn(SingletonComponent.class)
public class UserRepositoryModule {

    @Provides
    @Singleton
    public UserRepository bindUserRepository(@ApplicationContext Context context) {
        return new UserRepositoryImpl(context);
    }
}

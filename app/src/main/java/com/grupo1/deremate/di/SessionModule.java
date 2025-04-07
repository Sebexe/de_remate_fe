package com.grupo1.deremate.di;

import com.grupo1.deremate.session.SessionManager;
import com.grupo1.deremate.session.SessionManagerImpl;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class SessionModule {
    @Binds
    @Singleton
    public abstract SessionManager provideSessionManager(SessionManagerImpl implementation);
}

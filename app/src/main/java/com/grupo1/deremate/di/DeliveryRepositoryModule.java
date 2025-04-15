package com.grupo1.deremate.di; // O el paquete donde pongas tus módulos DI

import com.grupo1.deremate.repository.DeliveryRepository;
import com.grupo1.deremate.repository.impl.DeliveryRepositoryImpl;
import javax.inject.Singleton;
import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent; // O ActivityRetainedComponent si prefieres que viva con el ViewModel

@Module
@InstallIn(SingletonComponent.class) // Instalar en el componente Singleton (vive durante toda la app)
public abstract class DeliveryRepositoryModule {

    @Binds // Usamos @Binds para decirle a Hilt que cuando pida DeliveryRepository, use DeliveryRepositoryImpl
    @Singleton // El scope debe coincidir con el de la implementación si es necesario
    public abstract DeliveryRepository bindDeliveryRepository(
            DeliveryRepositoryImpl impl // Hilt sabe cómo crear DeliveryRepositoryImpl porque tiene @Inject constructor
    );

    // Puedes añadir aquí otros bindings de repositorios si los tienes
}
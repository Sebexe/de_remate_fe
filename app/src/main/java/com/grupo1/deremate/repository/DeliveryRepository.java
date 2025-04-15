package com.grupo1.deremate.repository;

import androidx.lifecycle.LiveData;
import com.grupo1.deremate.models.DeliveryDTO; // Asegúrate que la ruta es correcta
import java.util.List;

public interface DeliveryRepository {

    // Expone los datos como LiveData para que el ViewModel los observe
    LiveData<List<DeliveryDTO>> getDeliveriesLiveData();
    LiveData<Boolean> isLoadingLiveData();
    LiveData<String> getErrorLiveData();

    // Método para iniciar la carga de datos para un usuario específico
    void fetchDeliveriesForUser(Long userId);
}
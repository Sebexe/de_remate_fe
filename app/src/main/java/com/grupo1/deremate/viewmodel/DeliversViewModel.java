package com.grupo1.deremate.viewmodel;

import androidx.lifecycle.LiveData;
// Ya no necesitamos MutableLiveData aquí si los exponemos desde el repo
import androidx.lifecycle.ViewModel;
// Ya no necesitamos ApiClient, Callback, Response, etc.
import com.grupo1.deremate.models.DeliveryDTO; // Asegúrate que la ruta es correcta
import com.grupo1.deremate.repository.DeliveryRepository; // Importa la INTERFAZ del repositorio
import java.util.List;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
import android.util.Log;

@HiltViewModel
public class DeliversViewModel extends ViewModel {

    private static final String TAG = "DeliversViewModel";


    private final DeliveryRepository deliveryRepository;


    @Inject
    public DeliversViewModel(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
        Log.d(TAG, "DeliversViewModel created, DeliveryRepository provided: " + (deliveryRepository != null));
    }


    public LiveData<List<DeliveryDTO>> getDeliveries() {
        return deliveryRepository.getDeliveriesLiveData();
    }

    public LiveData<Boolean> getIsLoading() {
        return deliveryRepository.isLoadingLiveData();
    }

    public LiveData<String> getErrorMessage() {
        return deliveryRepository.getErrorLiveData();
    }


    public void fetchDeliveriesForUser(Long userId) {
        Log.d(TAG, "fetchDeliveriesForUser called in ViewModel for userId: " + userId + ". Delegating to repository.");

        deliveryRepository.fetchDeliveriesForUser(userId);
    }
}
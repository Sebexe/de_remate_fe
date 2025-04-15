package com.grupo1.deremate.repository.impl;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.grupo1.deremate.apis.DeliveryControllerApi; // Asegúrate que la interfaz Api existe y es correcta
import com.grupo1.deremate.infrastructure.ApiClient;
import com.grupo1.deremate.models.DeliveryDTO; // Asegúrate que la ruta es correcta
import com.grupo1.deremate.repository.DeliveryRepository;

import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.util.Log;


@Singleton
public class DeliveryRepositoryImpl implements DeliveryRepository {

    private static final String TAG = "DeliveryRepoImpl";
    private final ApiClient apiClient;
    private DeliveryControllerApi deliveryApi;


    private final MutableLiveData<List<DeliveryDTO>> deliveriesLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();



    @Inject
    public DeliveryRepositoryImpl(ApiClient apiClient) { // <-- Pide ApiClient como parámetro
        Log.d(TAG, "DeliveryRepositoryImpl constructor - ApiClient injected: " + (apiClient != null));
        // Hilt provee la instancia de ApiClient aquí.
        if (apiClient == null) {
            // Esto no debería ocurrir si Hilt está bien configurado, pero es una buena verificación.
            Log.e(TAG, "CRITICAL: ApiClient provided by Hilt is NULL in constructor!");
            // Lanzar excepción o manejar el error de forma que la app no continúe en estado inválido.
            throw new IllegalStateException("ApiClient cannot be null in DeliveryRepositoryImpl constructor");
        }
        // Guarda la referencia si la necesitas en otros métodos (opcional)
        this.apiClient = apiClient;

        // Ahora puedes usar el apiClient inyectado para crear el servicio API
        this.deliveryApi = apiClient.createService(DeliveryControllerApi.class);
        Log.d(TAG, "DeliveryControllerApi created: " + (this.deliveryApi != null));

        // Ya NO necesitas el campo @Inject ApiClient apiClient; separado.
    }

    // --- Implementación de la interfaz ---

    @Override
    public LiveData<List<DeliveryDTO>> getDeliveriesLiveData() {
        return deliveriesLiveData;
    }

    @Override
    public LiveData<Boolean> isLoadingLiveData() {
        return isLoadingLiveData;
    }

    @Override
    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    @Override
    public void fetchDeliveriesForUser(Long userId) {
        if (userId == null || userId <= 0) {
            Log.e(TAG, "fetchDeliveriesForUser: Invalid userId provided: " + userId);
            errorLiveData.postValue("ID de usuario inválido para el repositorio."); // Usa postValue si llamas desde otro hilo
            return;
        }
        if (deliveryApi == null) {
            Log.e(TAG, "fetchDeliveriesForUser: deliveryApi is null! Check ApiClient injection/initialization.");
            errorLiveData.postValue("Error interno: Servicio API no disponible.");
            return;
        }

        Log.d(TAG, "Fetching deliveries from API for userId: " + userId);
        isLoadingLiveData.postValue(true);
        errorLiveData.postValue(null); // Limpiar error anterior


        deliveryApi.getPackagesByUserId(userId).enqueue(new Callback<List<DeliveryDTO>>() {
            @Override
            public void onResponse(Call<List<DeliveryDTO>> call, Response<List<DeliveryDTO>> response) {
                isLoadingLiveData.postValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "API call successful for userId: " + userId + ". Count: " + response.body().size());
                    deliveriesLiveData.postValue(response.body());
                    if (response.body().isEmpty()) {
                        Log.d(TAG, "No deliveries found for userId: " + userId);

                    }
                } else {
                    String errorMsg = "Error en API: " + response.code();
                    try {
                        if(response.errorBody() != null) {
                            errorMsg += " - " + response.errorBody().string();
                        } else {
                            errorMsg += " - " + response.message();
                        }
                    } catch (Exception e) { Log.e(TAG, "Error leyendo errorBody", e); }
                    Log.e(TAG, "API call failed for userId: " + userId + ". Error: " + errorMsg);
                    errorLiveData.postValue(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<List<DeliveryDTO>> call, Throwable t) {
                isLoadingLiveData.postValue(false);
                String networkErrorMsg = "Error de Red: " + t.getMessage();
                Log.e(TAG, "API call failed for userId: " + userId + ". Network error.", t);
                errorLiveData.postValue(networkErrorMsg);
            }
        });
    }
}
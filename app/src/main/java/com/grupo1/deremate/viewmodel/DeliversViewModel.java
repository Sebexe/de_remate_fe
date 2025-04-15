package com.grupo1.deremate.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.grupo1.deremate.apis.DeliveryControllerApi;
import com.grupo1.deremate.models.DeliveryDTO;
import com.grupo1.deremate.infrastructure.ApiClient;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.util.Log; // For logging

import javax.inject.Inject;

public class DeliversViewModel extends ViewModel {

    private static final String TAG = "DeliversViewModel"; // For logging

    private final MutableLiveData<List<DeliveryDTO>> deliveries = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    @Inject
    ApiClient apiClient;

    private DeliveryControllerApi deliveryControllerApi;

    public DeliversViewModel() {
        deliveryControllerApi = apiClient.createService(DeliveryControllerApi.class);
    }

    public LiveData<List<DeliveryDTO>> getDeliveries() {
        return deliveries;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void fetchDeliveriesForUser(Long userId) {
        if (userId == null) {
            errorMessage.setValue("User ID cannot be null");
            return;
        }

        isLoading.setValue(true);
        errorMessage.setValue(null); // Clear previous errors

        deliveryControllerApi.getPackagesByUserId(userId).enqueue(new Callback<List<DeliveryDTO>>() {
            @Override
            public void onResponse(Call<List<DeliveryDTO>> call, Response<List<DeliveryDTO>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    deliveries.setValue(response.body());
                    if (response.body().isEmpty()) {
                        errorMessage.setValue("No deliveries found for this user.");
                    }
                } else {
                    // Handle API errors (e.g., 404 Not Found, 500 Server Error)
                    String errorMsg = "Failed to fetch deliveries. Code: " + response.code();
                    if (response.errorBody() != null) {
                        try {
                            // Try to parse specific error message from backend if available
                            errorMsg += " - " + response.errorBody().string();
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing error body", e);
                        }
                    }
                    errorMessage.setValue(errorMsg);
                    Log.e(TAG, errorMsg); // Log the detailed error
                }
            }

            @Override
            public void onFailure(Call<List<DeliveryDTO>> call, Throwable t) {
                isLoading.setValue(false);
                // Handle network errors (e.g., no connection)
                errorMessage.setValue("Network error: " + t.getMessage());
                Log.e(TAG, "Network error fetching deliveries", t); // Log the exception
            }
        });
    }
}
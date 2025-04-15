package com.grupo1.deremate.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar; // Import ProgressBar
import android.widget.TextView; // Import TextView
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider; // Import ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager; // Import LayoutManager
import androidx.recyclerview.widget.RecyclerView; // Import RecyclerView

// --- Importaciones necesarias ---
import com.grupo1.deremate.R; // Import R
import com.grupo1.deremate.infrastructure.DeliveriesAdapter; // Import Adapter
import com.grupo1.deremate.databinding.FragmentDeliversBinding;
import com.grupo1.deremate.models.DeliveryDTO;
import com.grupo1.deremate.viewmodel.DeliversViewModel; // Import ViewModel
import com.grupo1.deremate.repository.TokenRepository; // Importar TokenRepository
import javax.inject.Inject; // Importar Inject
import androidx.lifecycle.Observer;

import java.util.List;

// --- Asumiendo Hilt para inyección (si no, ajusta la inyección) ---
// import dagger.hilt.android.AndroidEntryPoint;

// @AndroidEntryPoint // Descomenta si usas Hilt
public class DeliversFragment extends Fragment {

    private static final String TAG = "DeliversFragment"; // For logging
    // Ya no necesitamos ARG_USER_ID

    private FragmentDeliversBinding binding;
    private DeliversViewModel viewModel;
    private DeliveriesAdapter adapter;
    // La variable userId ya no es necesaria como campo si la usamos directamente
    // private Long userId;

    // --- Inyectar TokenRepository ---
    @Inject
    TokenRepository tokenRepository;

    // --- Ya no necesitamos newInstance con argumento ---
    // Puedes tener un newInstance() vacío si quieres, o no tenerlo
    public static DeliversFragment newInstance() {
        return new DeliversFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // --- Ya no leemos argumentos aquí ---
        // Inicializar ViewModel
        viewModel = new ViewModelProvider(this).get(DeliversViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDeliversBinding.inflate(inflater, container, false);
        setupRecyclerView();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        observeViewModel();
        // --- Lógica movida para obtener ID y cargar datos ---
        loadDataBasedOnToken();
    }

    /**
     * Intenta obtener el User ID del token almacenado y luego
     * pide al ViewModel que cargue las entregas.
     */
    private void loadDataBasedOnToken() {
        if (tokenRepository == null) {
            // Esto no debería pasar si la inyección funciona, pero es una comprobación segura
            Log.e(TAG, "TokenRepository is null! Check Dagger/Hilt injection.");
            showError(getString(R.string.error_generic)); // Usa un string de error genérico
            return;
        }

        // Obtenemos el ID llamando al método del TokenRepository
        Long currentUserId = tokenRepository.getUserIdFromToken();

        // Verificamos si obtuvimos un ID válido
        if (currentUserId != null && currentUserId > 0) { // Check si es un ID > 0
            Log.d(TAG, "User ID obtained from TokenRepository: " + currentUserId);
            // Llamamos al ViewModel para que busque las entregas
            viewModel.fetchDeliveriesForUser(currentUserId);
        } else {
            // No se pudo obtener un ID válido del token
            Log.e(TAG, "Failed to get valid User ID from token. User might not be logged in or token is invalid/missing claim.");
            // Mostrar error en la UI
            showError(getString(R.string.error_loading_user_id)); // Crea este string en strings.xml
        }
    }

    private void setupRecyclerView() {
        // El setup del adapter no cambia
        adapter = new DeliveriesAdapter();
        binding.recyclerViewDeliveries.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewDeliveries.setAdapter(adapter);
    }

    private void observeViewModel() {

        // --- Observar estado de carga (isLoading) ---
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isLoading) {
                // Añadir chequeo por si binding es null (aunque getViewLifecycleOwner debería prevenirlo)
                if (binding == null) return;

                // La lógica original para mostrar/ocultar ProgressBar está bien
                if (isLoading != null) {
                    binding.progressBarLoading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                    if (isLoading) {
                        // Ocultar otros elementos mientras carga
                        binding.textViewError.setVisibility(View.GONE);
                        binding.recyclerViewDeliveries.setVisibility(View.GONE);
                    }
                }
            }
        });

        // --- Observar datos de entregas (deliveries) ---
        viewModel.getDeliveries().observe(getViewLifecycleOwner(), new Observer<List<DeliveryDTO>>() { // Reemplaza DeliveryDTO con tu clase real si es diferente
            @Override
            public void onChanged(@Nullable List<DeliveryDTO> deliveries) { // Reemplaza DeliveryDTO
                // Añadir chequeo por si binding es null
                if (binding == null) return;

                // Lógica original para actualizar la lista o mostrar mensaje de vacío
                if (deliveries != null && !deliveries.isEmpty()) {
                    adapter.submitList(deliveries);
                    binding.recyclerViewDeliveries.setVisibility(View.VISIBLE);
                    // Asegurarse de ocultar el error si llegan datos
                    binding.textViewError.setVisibility(View.GONE);
                    binding.textViewError.setTag(null); // Limpiar tag por si acaso
                    Log.d(TAG, "Deliveries updated: " + deliveries.size() + " items");
                } else if (deliveries != null && deliveries.isEmpty()) {
                    // Solo mostrar "No se encontraron entregas" si no hay ya un error más importante visible
                    boolean isErrorAlreadyVisible = binding.textViewError.getVisibility() == View.VISIBLE;
                    Boolean loading = viewModel.getIsLoading().getValue(); // Comprobar si aún está cargando
                    boolean isLoading = (loading != null && loading);

                    if (!isErrorAlreadyVisible && !isLoading) {
                        // Usar el helper showError para consistencia
                        showError(getString(R.string.no_deliveries_found));
                        Log.d(TAG, "Deliveries list is empty.");
                    }
                    // Si ya hay un error visible o está cargando, no mostramos este mensaje
                    binding.recyclerViewDeliveries.setVisibility(View.GONE); // Asegurar que la lista está oculta
                }
                // Si deliveries es null, usualmente esperamos a que isLoading o errorMessage cambien.
            }
        });
    }

// Asegúrate de que el método showError existe y funciona como antes:
    /**
     * Helper method to show an error message in the UI.
     * @param message The error message to display.
     */
    private void showError(String message) {
        // Añadir chequeo por si binding es null
        if (binding == null) return;

        binding.progressBarLoading.setVisibility(View.GONE);
        binding.recyclerViewDeliveries.setVisibility(View.GONE);
        binding.textViewError.setText(message);
        binding.textViewError.setVisibility(View.VISIBLE);
        // Usamos un tag para diferenciar errores, opcional pero útil para la lógica anterior
        if (message.equals(getString(R.string.error_loading_user_id)) || message.equals(getString(R.string.error_generic))) {
            binding.textViewError.setTag("user_id_error");
        } else {
            binding.textViewError.setTag(null); // Limpiar tag para otros errores
        }
    }

// --- ¡IMPORTANTE! ---
// Si después de este cambio el error "Cannot resolve method 'observe(...)'" persiste,
// el problema casi con seguridad está en tus dependencias de Gradle o en la
// configuración de compatibilidad con Java 8. Revisa esos puntos de nuevo.


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Important for view binding in fragments
    }
}
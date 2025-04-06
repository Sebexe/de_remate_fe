package com.grupo1.deremate.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.grupo1.deremate.apis.RouteControllerApi;
import com.grupo1.deremate.infrastructure.ApiClient;
import com.grupo1.deremate.databinding.FragmentHomeBinding;
import com.grupo1.deremate.enums.NeighborhoodsCABA;
import com.grupo1.deremate.models.AvailableRouteDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private NeighborhoodsCABA selectedOrigin;
    private NeighborhoodsCABA selectedDestination;
    ApiClient apiClient = new ApiClient("http://10.0.2.2:8080", "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJkZWJlcmFyZGlzdG9tYXNAZ21haWwuY29tIiwiaWF0IjoxNzQzOTcyODkyLCJleHAiOjE3NDM5NzY0OTJ9.bnTPz2ldh9-oafJqnGzrJ3WaLZMisPUIKoEwYfIaHiw");
    RouteControllerApi routeApi = apiClient.createService(RouteControllerApi.class);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        setupSpinners();
        setupButton();
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setupSpinners() {
        List<String> barrios = NeighborhoodsCABA.Companion.getNeighborhoodNames();
        barrios.add(0, "Seleccione un barrio");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                barrios
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        binding.spinnerOrigin.setAdapter(adapter);
        binding.spinnerDestination.setAdapter(adapter);
        binding.spinnerOrigin.setSelection(0, false);
        binding.spinnerDestination.setSelection(0, false);

        binding.spinnerOrigin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOriginName = (String) parent.getItemAtPosition(position);
                selectedOrigin = findBarrioByName(selectedOriginName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        binding.spinnerDestination.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedDestinationName = (String) parent.getItemAtPosition(position);
                selectedDestination = findBarrioByName(selectedDestinationName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
    private NeighborhoodsCABA findBarrioByName(String name) {
        for (NeighborhoodsCABA barrio : NeighborhoodsCABA.getEntries()) {
            if (barrio.name().equalsIgnoreCase(name)) {
                return barrio;
            }
        }
        return null;
    }

    private void setupButton() {
        binding.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayRoutes();
            }
        });
    }

    private void displayRoutes() {
        String originName = selectedOrigin != null ? selectedOrigin.name() : null;
        String destinationName = selectedDestination != null ? selectedDestination.name() : null;

        Call<List<AvailableRouteDTO>> call = routeApi.getAvailableRoutes(originName, destinationName);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<AvailableRouteDTO>> call, Response<List<AvailableRouteDTO>> response) {
                Log.d("Response","Response: "+response.toString());
                if (response.isSuccessful()) {
                    List<AvailableRouteDTO> availableRoutes = response.body();
                    StringBuilder resultText = new StringBuilder();
                    if (availableRoutes == null || availableRoutes.isEmpty()) {
                        binding.tvResult.setVisibility(View.GONE);
                        binding.tvNoRoutes.setVisibility(View.VISIBLE);
                    } else {
                        binding.tvResult.setVisibility(View.INVISIBLE);
                        binding.tvNoRoutes.setVisibility(View.GONE);
                        for (AvailableRouteDTO route : availableRoutes) {
                            resultText.append("Origen: ").append(route.getOrigin()).append("\n");
                            resultText.append("Destino: ").append(route.getDestination()).append("\n");
                            resultText.append("Status: ").append(route.getStatus()).append("\n\n");
                        }
                    }
                    binding.tvResult.setText(resultText.toString());
                    binding.tvResult.setVisibility(View.VISIBLE);
                } else {
                    String error = null;
                    try{
                        error= response.errorBody().string();
                    }catch (Exception ex){
                        error= "no se pudo obtener el mensaje de error";
                    }

                    Toast.makeText(requireContext(), "Error: " + error, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<AvailableRouteDTO>> call, Throwable t) {
                Toast.makeText(requireContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}
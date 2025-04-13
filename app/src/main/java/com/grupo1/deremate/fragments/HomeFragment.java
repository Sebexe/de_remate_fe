package com.grupo1.deremate.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.grupo1.deremate.apis.DeliveryControllerApi;
import com.grupo1.deremate.databinding.FragmentHomeBinding;
import com.grupo1.deremate.infrastructure.ApiClient;
import com.grupo1.deremate.infrastructure.PackageAdapter;
import com.grupo1.deremate.models.PackageDTO;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private List<PackageDTO> allPackages = new ArrayList<>();

    @Inject
    ApiClient apiClient;
    DeliveryControllerApi deliveryApi;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        deliveryApi = apiClient.createService(DeliveryControllerApi.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        loadPackages();
        setupFilters();

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void loadPackages() {
        Call<List<PackageDTO>> call = deliveryApi.getPackagesInWarehouse();

        call.enqueue(new Callback<List<PackageDTO>>() {
            @Override
            public void onResponse(Call<List<PackageDTO>> call, Response<List<PackageDTO>> response) {
                if (response.isSuccessful()) {
                    allPackages = response.body();
                    if (allPackages != null) {
                        showPackages(allPackages);
                    }
                } else {
                    Toast.makeText(requireContext(), "Error al cargar paquetes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<PackageDTO>> call, Throwable t) {
                Toast.makeText(requireContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showPackages(List<PackageDTO> packages) {
        binding.rvPackages.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvPackages.setAdapter(new PackageAdapter(requireContext(), packages));
    }

    private void setupFilters() {
        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters();
            }
            @Override public void afterTextChanged(Editable s) {}
        };

        binding.etPackageCode.addTextChangedListener(watcher);
        binding.etSector.addTextChangedListener(watcher);
        binding.etShelf.addTextChangedListener(watcher);
    }

    private void applyFilters() {
        String code = binding.etPackageCode.getText().toString().trim();
        String sector = binding.etSector.getText().toString().trim();
        String shelf = binding.etShelf.getText().toString().trim();

        List<PackageDTO> filtered = new ArrayList<>();
        for (PackageDTO pkg : allPackages) {
            boolean matchCode = code.isEmpty() || String.valueOf(pkg.getId()).contains(code);
            boolean matchSector = sector.isEmpty() || pkg.getPackageLocation().toLowerCase().contains(("sector " + sector).toLowerCase());
            boolean matchShelf = shelf.isEmpty() || pkg.getPackageLocation().toLowerCase().contains(("estante " + shelf).toLowerCase());

            if (matchCode && matchSector && matchShelf) {
                filtered.add(pkg);
            }
        }

        showPackages(filtered);
    }
}

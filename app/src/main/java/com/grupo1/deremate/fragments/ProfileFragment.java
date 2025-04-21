package com.grupo1.deremate.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.grupo1.deremate.LoginActivity;
import com.grupo1.deremate.databinding.FragmentProfileBinding;
import com.grupo1.deremate.models.UserDTO;
import com.grupo1.deremate.repository.TokenRepository;
import com.grupo1.deremate.repository.UserRepository;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;

    @Inject
    TokenRepository tokenRepository;

    @Inject
    UserRepository userRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        // Mostrar datos del usuario
        UserDTO user = userRepository.getUser();
        if (user != null) {
            binding.tvUserEmail.setText("Email: " + user.getEmail());
            binding.tvUserName.setText("Nombre: " + user.getFirstname() + " " + user.getLastname());
            binding.tvUserVerified.setText("Email verificado: " + (Boolean.TRUE.equals(user.isEmailVerified()) ? "Sí" : "No"));
        }

        // Botón cerrar sesión
        binding.btnLogout.setOnClickListener(v -> {
            tokenRepository.clearToken();
            userRepository.clearUser();

            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

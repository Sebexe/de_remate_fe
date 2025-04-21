package com.grupo1.deremate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.grupo1.deremate.R; // Importa tu R
import com.grupo1.deremate.databinding.ActivityForgotPasswordBinding; // Importa tu ViewBinding
import com.grupo1.deremate.fragments.EnterCodeFragment;
import com.grupo1.deremate.fragments.EnterEmailFragment;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ForgotPasswordActivity extends AppCompatActivity
implements EnterEmailFragment.OnCodeSentListener, EnterCodeFragment.OnPasswordResetListener {


    private ActivityForgotPasswordBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.tvForgotPasswordTitle.setText(getString(R.string.forgotPasswordTitle));


        if (savedInstanceState == null) {
            loadEnterEmailFragment();
        }
    }

    private void loadEnterEmailFragment() {
        EnterEmailFragment enterEmailFragment = new EnterEmailFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.replace(R.id.forgot_password_fragment_container, enterEmailFragment);
        transaction.commit();
    }

    // --- Implementación de Callbacks de los Fragments ---

    /**
     * Callback llamado desde EnterEmailFragment cuando el código se envía correctamente.
     * Reemplaza el fragment actual por EnterCodeFragment.
     * @param email El correo electrónico ingresado por el usuario.
     */
    @Override
    public void onCodeSent(String email) {
        Log.d("ForgotPasswordActivity", "Código enviado para: " + email + ". Cargando EnterCodeFragment.");


        EnterCodeFragment enterCodeFragment = EnterCodeFragment.newInstance(email);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.forgot_password_fragment_container, enterCodeFragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    /**
     * Callback llamado desde EnterCodeFragment cuando la contraseña se restablece con éxito.
     * Muestra un mensaje y navega de vuelta a LoginActivity.
     */
    @Override
    public void onPasswordResetSuccess() {
        Log.d("ForgotPasswordActivity", "Password reset exitoso. Navegando a Login.");
        Toast.makeText(this, R.string.resetPasswordSuccess, Toast.LENGTH_LONG).show();
        // Crear Intent para volver a LoginActivity
        Intent intent = new Intent(this, LoginActivity.class);
        // Limpiar el stack para que el usuario no vuelva aquí con el botón Back
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Callback llamado desde EnterCodeFragment si falla el restablecimiento de contraseña.
     * @param errorMsg Mensaje de error descriptivo.
     */
    @Override
    public void onPasswordResetFailure(String errorMsg) {

        Log.e("ForgotPasswordActivity", "Fallo al restablecer contraseña: " + errorMsg);

    }

    // --- Limpieza ---

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
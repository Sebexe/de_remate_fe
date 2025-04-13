package com.grupo1.deremate; // Asegúrate que el paquete sea el correcto

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
// Importa la clase LoginActivity si está en otro paquete
// import com.grupo1.deremate.LoginActivity;


// Implementa las interfaces de listener definidas en los Fragments Java
@AndroidEntryPoint
public class ForgotPasswordActivity extends AppCompatActivity
implements EnterEmailFragment.OnCodeSentListener, EnterCodeFragment.OnPasswordResetListener {

    // View Binding
    private ActivityForgotPasswordBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflar layout usando View Binding
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Establecer el título inicial (opcional)
        binding.tvForgotPasswordTitle.setText(getString(R.string.forgotPasswordTitle));

        // Cargar el fragment inicial solo si la Activity se está creando por primera vez
        // Si se restaura (ej. por rotación), el FragmentManager se encarga de restaurar el estado
        if (savedInstanceState == null) {
            loadEnterEmailFragment();
        }
    }

    // Método para cargar el primer fragment (ingresar email)
    private void loadEnterEmailFragment() {
        EnterEmailFragment enterEmailFragment = new EnterEmailFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // Reemplaza el contenido del contenedor con el fragment
        transaction.replace(R.id.forgot_password_fragment_container, enterEmailFragment);
        // No añadir al back stack para que al presionar Back se cierre la Activity
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

        // Usar el método factory estático para crear el fragment y pasar el email
        EnterCodeFragment enterCodeFragment = EnterCodeFragment.newInstance(email);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // Reemplazar el fragment actual
        transaction.replace(R.id.forgot_password_fragment_container, enterCodeFragment);
        // Añadir la transacción al back stack para que el usuario pueda volver al paso anterior
        transaction.addToBackStack(null);
        transaction.commit();

        // Opcional: Cambiar el título de la Activity
        // binding.tvForgotPasswordTitle.setText("Ingresar Código");
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
        finish(); // Cerrar esta ForgotPasswordActivity
    }

    /**
     * Callback llamado desde EnterCodeFragment si falla el restablecimiento de contraseña.
     * @param errorMsg Mensaje de error descriptivo.
     */
    @Override
    public void onPasswordResetFailure(String errorMsg) {
        // El fragment ya mostró un Toast específico. Aquí solo registramos el error.
        Log.e("ForgotPasswordActivity", "Fallo al restablecer contraseña: " + errorMsg);
        // Opcionalmente, podrías mostrar un diálogo o Snackbar desde aquí si prefieres
        Toast.makeText(this, "Ocurrió un error al restablecer", Toast.LENGTH_SHORT).show();
    }

    // --- Limpieza ---

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Liberar la referencia al binding para evitar memory leaks
        binding = null;
    }
}
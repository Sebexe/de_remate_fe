package com.grupo1.deremate.fragments; // Ajusta tu paquete si es diferente

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.grupo1.deremate.R; // Importa tu R
import com.grupo1.deremate.apis.AuthControllerApi; // Importa tu CLASE Java API
import com.grupo1.deremate.models.GenericResponseDTOString;
import com.grupo1.deremate.models.PasswordResetRequestDto; // Importa tu DTO
import com.grupo1.deremate.infrastructure.ApiClient;
import com.grupo1.deremate.databinding.FragmentEnterCodeBinding; // Importa tu ViewBinding

import org.json.JSONObject;
import org.json.JSONException; // Importar JSONException

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class EnterCodeFragment extends Fragment {

    private static final String TAG = "EnterCodeFragment"; // Tag para Logs

    @Inject
    ApiClient apiClient;

    private AuthControllerApi authControllerApi;

    public static final String ARG_EMAIL = "USER_EMAIL";

    public interface OnPasswordResetListener {
        void onPasswordResetSuccess();
        void onPasswordResetFailure(String errorMsg);
    }

    private OnPasswordResetListener listener;
    private FragmentEnterCodeBinding binding;
    private String userEmail;

    public static EnterCodeFragment newInstance(String email) {
        EnterCodeFragment fragment = new EnterCodeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EMAIL, email);
        fragment.setArguments(args);
        return fragment;
    }

    // --- Ciclo de Vida y Setup ---

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Asegurarse de que la actividad implementa el listener
        if (context instanceof OnPasswordResetListener) {
            listener = (OnPasswordResetListener) context;
        } else {
            throw new ClassCastException(context + " must implement OnPasswordResetListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Obtener email de los argumentos
        if (getArguments() != null) {
            userEmail = getArguments().getString(ARG_EMAIL);
        }
        if (userEmail == null || userEmail.isEmpty()) {
            Log.e(TAG, "CRITICAL: Email argument was null or empty in onCreate.");
            // Considerar notificar al listener o mostrar error aquí si es posible
            // listener.onPasswordResetFailure("Error interno: Email no recibido.");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEnterCodeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializar servicio API
        if (apiClient != null) { // Chequeo extra por si la inyección fallara
            authControllerApi = apiClient.createService(AuthControllerApi.class);
        } else {
            Log.e(TAG, "ApiClient was null in onViewCreated! Check Hilt setup.");
            Toast.makeText(getContext(), "Error de configuración interna.", Toast.LENGTH_SHORT).show();
            // Podrías llamar a listener.onPasswordResetFailure aquí
            return; // No continuar si apiClient es null
        }

        // Configurar texto de instrucciones
        if (userEmail != null) {
            binding.tvEnterCodeInstructions.setText(getString(R.string.enterCodePrompt, userEmail));
        } else {
            // Usar texto alternativo si userEmail es null (aunque no debería llegar aquí si onCreate lo valida)
            binding.tvEnterCodeInstructions.setText(getString(R.string.enterCodePrompt, "tu correo"));
            Log.w(TAG, "User email was null in onViewCreated, using fallback text.");
        }

        // Configurar botón
        binding.btnConfirmReset.setOnClickListener(v -> attemptPasswordReset());
    }

    // --- Lógica de la API ---

    private void attemptPasswordReset() {
        // Re-validar email por si acaso
        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(getContext(), "Error interno: Email no disponible.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Limpiar errores previos antes de validar
        binding.etResetCode.setError(null);
        binding.etNewPassword.setError(null);
        binding.etConfirmPassword.setError(null);

        String code = binding.etResetCode.getText().toString().trim();
        String newPassword = binding.etNewPassword.getText().toString(); // No quitar espacios aquí por si son válidos
        String confirmPassword = binding.etConfirmPassword.getText().toString();

        // Realizar validaciones frontend (incluyendo las de contraseña)
        if (!validateInputs(code, newPassword, confirmPassword)) {
            return; // Detener si la validación falla
        }

        setLoading(true); // Mostrar estado de carga

        // Crear DTO y llamar a la API
        PasswordResetRequestDto request = new PasswordResetRequestDto(code, userEmail, newPassword);
        Log.d(TAG, "Attempting password reset with request for email: " + userEmail);
        // System.out.println(request.toString()); // Mejor usar Log.d

        // Asegurarse que el servicio API no es null
        if (authControllerApi == null) {
            Log.e(TAG, "attemptPasswordReset: authControllerApi is null!");
            Toast.makeText(getContext(), "Error interno: Servicio API no disponible.", Toast.LENGTH_SHORT).show();
            setLoading(false);
            return;
        }

        authControllerApi.resetPassword(request).enqueue(new Callback<GenericResponseDTOString>() {
            @Override
            public void onResponse(@NonNull Call<GenericResponseDTOString> call, @NonNull Response<GenericResponseDTOString> response) {
                // Chequeo defensivo por si el fragment se destruye mientras la llamada está en curso
                if (!isAdded() || binding == null) return;
                setLoading(false); // Ocultar estado de carga

                if (response.isSuccessful() && response.body() != null) {
                    // Éxito
                    Log.d(TAG, "Password reset successful for " + userEmail);
                    if (listener != null) {
                        listener.onPasswordResetSuccess(); // Notificar a la actividad para que navegue
                    }
                } else {
                    // Fallo (ej: token inválido, error del servidor)
                    String errorMsg = parseErrorMessage(response.errorBody());
                    Log.w(TAG, "Password reset failed: Code=" + response.code() + " - " + errorMsg);
                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
                    if (listener != null) {
                        listener.onPasswordResetFailure(errorMsg); // Notificar a la actividad (pero no navegará)
                    }
                    // Opcional: Limpiar campos o enfocar el código de nuevo
                    binding.etResetCode.requestFocus();
                    // binding.etResetCode.setText(""); // Podrías limpiar el código si falla
                }
            }

            @Override
            public void onFailure(@NonNull Call<GenericResponseDTOString> call, @NonNull Throwable t) {
                // Chequeo defensivo
                if (!isAdded() || binding == null) return;
                setLoading(false); // Ocultar estado de carga

                // Fallo de Red
                Log.e(TAG, "Password reset network failure", t);
                String networkErrorMsg = getString(R.string.enterCodeNetworkError) + ": " + t.getMessage();
                Toast.makeText(getContext(), networkErrorMsg, Toast.LENGTH_LONG).show();
                if (listener != null) {
                    listener.onPasswordResetFailure(networkErrorMsg); // Notificar a la actividad (pero no navegará)
                }
            }
        });
    }

    // --- Helpers ---

    // Parser de Errores (ya maneja el caso "Invalid or expired token")
    private String parseErrorMessage(ResponseBody errorBody) {
        if (errorBody == null) {
            Log.w("ParseError", "Error body was null.");
            return getString(R.string.error_unknown_response);
        }
        try {

            String errorBodyString = errorBody.string();
            Log.d("ParseError", "Raw error body: " + errorBodyString);
            JSONObject jsonObject = new JSONObject(errorBodyString);

            if (jsonObject.has("message")) {
                return jsonObject.getString("message");
            }

            Log.w("ParseError", "Could not find 'message' key in error JSON.");
            return getString(R.string.error_parsing_fallback);

        } catch (JSONException e) {
            Log.e("ParseError", "Error parsing JSON error response: " + e.getMessage());
            return getString(R.string.error_invalid_response);
        } catch (Exception e) {
            Log.e("ParseError", "Error reading error body: " + e.getMessage());
            return getString(R.string.error_reading_response);
        }
    }

    // Validación de Inputs (CON validación de contraseña)
    private boolean validateInputs(String code, String pass1, String pass2) {
        boolean isValid = true;

        // Código
        if (TextUtils.isEmpty(code)) {
            binding.etResetCode.setError(getString(R.string.enterCodeCodeRequired));
            isValid = false;
        } else if (code.length() != 4) {
            binding.etResetCode.setError(getString(R.string.validation_code_length, 4)); // Nuevo String
            isValid = false;
        } else {
            binding.etResetCode.setError(null);
        }

        // Nueva Contraseña
        if (TextUtils.isEmpty(pass1)) {
            binding.etNewPassword.setError(getString(R.string.enterCodePasswordRequired));
            isValid = false;
        } else {
            boolean meetsRequirements = true;
            if (pass1.length() < 6) {
                binding.etNewPassword.setError(getString(R.string.validation_password_length_min, 6));
                isValid = false; // Marcar como inválido general también
                meetsRequirements = false;
            } else if (!pass1.matches(".*[A-Z].*")) {
                binding.etNewPassword.setError(getString(R.string.validation_password_uppercase));
                isValid = false;
                meetsRequirements = false;
            } else if (!pass1.matches(".*\\d.*")) {
                binding.etNewPassword.setError(getString(R.string.validation_password_digit));
                isValid = false;
                meetsRequirements = false;
            }

            if (meetsRequirements) {
                binding.etNewPassword.setError(null);
            }
        }

        // Confirmar Contraseña
        if (TextUtils.isEmpty(pass2)) {
            binding.etConfirmPassword.setError(getString(R.string.enterCodePasswordRequired));
            isValid = false;
        } else if (binding.etNewPassword.getError() == null && !pass1.equals(pass2)) { // Solo si pass1 fue válida
            binding.etConfirmPassword.setError(getString(R.string.enterCodePasswordsMismatch));
            isValid = false;
        } else if (binding.etNewPassword.getError() == null && pass1.equals(pass2)) {
            // Limpiar error si pass1 fue válida y coinciden
            binding.etConfirmPassword.setError(null);
        }
        // Si pass1 tuvo error, no limpiamos explícitamente el error de confirmación aquí

        return isValid;
    }

    // setLoading (sin cambios)
    private void setLoading(boolean isLoading) {
        if (binding != null) {
            binding.btnConfirmReset.setEnabled(!isLoading);
            binding.btnConfirmReset.setText(isLoading ?
                    getString(R.string.enterCodeResetting) :
                    getString(R.string.enterCodeConfirmBtn));
            // Opcional: deshabilitar campos mientras carga
            binding.etResetCode.setEnabled(!isLoading);
            binding.etNewPassword.setEnabled(!isLoading);
            binding.etConfirmPassword.setEnabled(!isLoading);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Liberar binding
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null; // Liberar listener
    }
}
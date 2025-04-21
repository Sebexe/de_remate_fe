package com.grupo1.deremate.fragments;
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

import com.grupo1.deremate.R;
import com.grupo1.deremate.apis.AuthControllerApi;
import com.grupo1.deremate.models.GenericResponseDTOString;
import com.grupo1.deremate.models.PasswordResetRequestDto;
import com.grupo1.deremate.infrastructure.ApiClient;
import com.grupo1.deremate.databinding.FragmentEnterCodeBinding;

import org.json.JSONObject;
import org.json.JSONException;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class EnterCodeFragment extends Fragment {

    private static final String TAG = "EnterCodeFragment";

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
        if (context instanceof OnPasswordResetListener) {
            listener = (OnPasswordResetListener) context;
        } else {
            throw new ClassCastException(context + " must implement OnPasswordResetListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            userEmail = getArguments().getString(ARG_EMAIL);
        }
        if (userEmail == null || userEmail.isEmpty()) {
            Log.e(TAG, "CRITICAL: Email argument was null or empty in onCreate.");

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
        if (apiClient != null) { // Chequeo extra por si la inyección falla
            authControllerApi = apiClient.createService(AuthControllerApi.class);
        } else {
            Log.e(TAG, "ApiClient was null in onViewCreated! Check Hilt setup.");
            Toast.makeText(getContext(), "Error de configuración interna.", Toast.LENGTH_SHORT).show();

            return;
        }


        if (userEmail != null) {
            binding.tvEnterCodeInstructions.setText(getString(R.string.enterCodePrompt, userEmail));
        } else {

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
        String newPassword = binding.etNewPassword.getText().toString();
        String confirmPassword = binding.etConfirmPassword.getText().toString();

        // Realizar validaciones frontend (incluyendo las de contraseña)
        if (!validateInputs(code, newPassword, confirmPassword)) {
            return;
        }

        setLoading(true);


        PasswordResetRequestDto request = new PasswordResetRequestDto(code, userEmail, newPassword);
        Log.d(TAG, "Attempting password reset with request for email: " + userEmail);



        if (authControllerApi == null) {
            Log.e(TAG, "attemptPasswordReset: authControllerApi is null!");
            Toast.makeText(getContext(), "Error interno: Servicio API no disponible.", Toast.LENGTH_SHORT).show();
            setLoading(false);
            return;
        }

        authControllerApi.resetPassword(request).enqueue(new Callback<GenericResponseDTOString>() {
            @Override
            public void onResponse(@NonNull Call<GenericResponseDTOString> call, @NonNull Response<GenericResponseDTOString> response) {

                if (!isAdded() || binding == null) return;
                setLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    // Éxito
                    Log.d(TAG, "Password reset successful for " + userEmail);
                    if (listener != null) {
                        listener.onPasswordResetSuccess();
                    }
                } else {

                    String errorMsg = parseErrorMessage(response.errorBody());
                    Log.w(TAG, "Password reset failed: Code=" + response.code() + " - " + errorMsg);
                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
                    if (listener != null) {
                        listener.onPasswordResetFailure(errorMsg); // Notificar a la actividad (pero no navegará)
                    }

                    binding.etResetCode.requestFocus();

                }
            }

            @Override
            public void onFailure(@NonNull Call<GenericResponseDTOString> call, @NonNull Throwable t) {

                if (!isAdded() || binding == null) return;
                setLoading(false); // Ocultar estado de carga


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


    private boolean validateInputs(String code, String pass1, String pass2) {
        boolean isValid = true;


        if (TextUtils.isEmpty(code)) {
            binding.etResetCode.setError(getString(R.string.enterCodeCodeRequired));
            isValid = false;
        } else if (code.length() != 4) {
            binding.etResetCode.setError(getString(R.string.validation_code_length, 4));
            isValid = false;
        } else {
            binding.etResetCode.setError(null);
        }


        if (TextUtils.isEmpty(pass1)) {
            binding.etNewPassword.setError(getString(R.string.enterCodePasswordRequired));
            isValid = false;
        } else {
            boolean meetsRequirements = true;
            if (pass1.length() < 6) {
                binding.etNewPassword.setError(getString(R.string.validation_password_length_min, 6));
                isValid = false;
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

            binding.etConfirmPassword.setError(null);
        }


        return isValid;
    }


    private void setLoading(boolean isLoading) {
        if (binding != null) {
            binding.btnConfirmReset.setEnabled(!isLoading);
            binding.btnConfirmReset.setText(isLoading ?
                    getString(R.string.enterCodeResetting) :
                    getString(R.string.enterCodeConfirmBtn));
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
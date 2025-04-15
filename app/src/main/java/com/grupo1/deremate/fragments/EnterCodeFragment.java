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
import com.grupo1.deremate.databinding.FragmentEnterCodeBinding; // Importa tu ViewBinding // Importa tu ErrorParser

import org.json.JSONObject;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class EnterCodeFragment extends Fragment {

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
            throw new ClassCastException(context.toString() + " must implement OnPasswordResetListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            userEmail = getArguments().getString(ARG_EMAIL);
        }
        if (userEmail == null || userEmail.isEmpty()) {
            Log.e("EnterCodeFragment", "CRITICAL: Email argument was null or empty in onCreate.");

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


        authControllerApi = apiClient.createService(AuthControllerApi.class);


        if (userEmail != null) {
            binding.tvEnterCodeInstructions.setText(getString(R.string.enterCodePrompt, userEmail));
        } else {
            binding.tvEnterCodeInstructions.setText(getString(R.string.enterCodePrompt, "tu correo"));
            Log.w("EnterCodeFragment", "User email was null in onViewCreated, using fallback text.");
        }


        binding.btnConfirmReset.setOnClickListener(v -> attemptPasswordReset());
    }

    // --- Lógica de la API ---

    private void attemptPasswordReset() {
        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(getContext(), "Error interno: Email no disponible.", Toast.LENGTH_SHORT).show();
            return;
        }

        String code = binding.etResetCode.getText().toString().trim();
        String newPassword = binding.etNewPassword.getText().toString();
        String confirmPassword = binding.etConfirmPassword.getText().toString();

        if (!validateInputs(code, newPassword, confirmPassword)) {
            return;
        }

        setLoading(true);


        PasswordResetRequestDto request = new PasswordResetRequestDto(code,userEmail, newPassword);
        System.out.println(request.toString());


        authControllerApi.resetPassword(request).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<GenericResponseDTOString> call, Response<GenericResponseDTOString> response) {
                if (!isAdded() || binding == null) return;
                setLoading(false);

                if (!response.isSuccessful()) {

                    String errorMsg = parseErrorMessage(response.errorBody());
                    Log.w("EnterCodeFragment", "Password reset failed: " + response.code() + " - " + errorMsg);
                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();

                    if (listener != null) {
                        listener.onPasswordResetFailure(errorMsg);
                    }
                    return;
                }


                Log.d("EnterCodeFragment", "Password reset successful for " + userEmail);

                if (listener != null) {
                    listener.onPasswordResetSuccess();
                }
            }

            @Override
            public void onFailure(Call<GenericResponseDTOString> call, Throwable t) {
                if (!isAdded() || binding == null) return;
                setLoading(false);
                Log.e("EnterCodeFragment", "Password reset network failure", t);
                String networkErrorMsg = "Error de red: " + (t.getMessage() != null ? t.getMessage() : getString(R.string.enterCodeNetworkError));
                Toast.makeText(getContext(), networkErrorMsg, Toast.LENGTH_LONG).show();
                if (listener != null) {
                    listener.onPasswordResetFailure(networkErrorMsg);
                }
            }
        });
    }

    // --- Helpers ---

    private String parseErrorMessage(ResponseBody errorBody){

        try {
            JSONObject json = new JSONObject(errorBody.string());
            return json.optString("message", "Error inesperado");
        } catch (Exception e) {
            Log.e("ParseError", "No se pudo parsear el error", e);
            return "Error desconocido del servidor";
        }


    }

    private boolean validateInputs(String code, String pass1, String pass2) {
        boolean isValid = true;


        if (TextUtils.isEmpty(code)) {
            binding.etResetCode.setError(getString(R.string.enterCodeCodeRequired));
            isValid = false;
        } else if (code.length() != 4) { // Asumiendo 4 caracteres
            binding.etResetCode.setError("El código debe tener 4 caracteres");
            isValid = false;
        } else {
            binding.etResetCode.setError(null);
        }


        if (TextUtils.isEmpty(pass1)) {
            binding.etNewPassword.setError(getString(R.string.enterCodePasswordRequired));
            isValid = false;
        } else {

            binding.etNewPassword.setError(null);
        }


        if (TextUtils.isEmpty(pass2)) {
            binding.etConfirmPassword.setError(getString(R.string.enterCodePasswordRequired));
            isValid = false;
        } else if (!pass1.equals(pass2)) {
            binding.etConfirmPassword.setError(getString(R.string.enterCodePasswordsMismatch));
            isValid = false;
        } else {
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
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
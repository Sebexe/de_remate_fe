package com.grupo1.deremate.fragments; // Ajusta tu paquete si es diferente

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.grupo1.deremate.R; // Importa tu R
import com.grupo1.deremate.apis.AuthControllerApi; // Importa tu CLASE Java API
import com.grupo1.deremate.infrastructure.ApiClient; // Importa tu ApiClient
import com.grupo1.deremate.databinding.FragmentEnterEmailBinding; // Importa tu ViewBinding
import com.grupo1.deremate.models.GenericResponseDTOObject;
import com.grupo1.deremate.models.GenericResponseDTOString;


import org.json.JSONObject;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class EnterEmailFragment extends Fragment {

    @Inject
    ApiClient apiClient;

    private AuthControllerApi authControllerApi;

    public interface OnCodeSentListener {
        void onCodeSent(String email);
    }

    private OnCodeSentListener listener;
    private FragmentEnterEmailBinding binding;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnCodeSentListener) {
            listener = (OnCodeSentListener) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement OnCodeSentListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEnterEmailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        authControllerApi = apiClient.createService(AuthControllerApi.class);


        binding.btnSendResetCode.setOnClickListener(v -> attemptSendCode());


        if (binding.btnGoBack != null) {
            binding.btnGoBack.setOnClickListener(v -> {

                if (getActivity() != null) {
                    getActivity().getOnBackPressedDispatcher().onBackPressed();
                }
            });
        }


    }

    private void attemptSendCode() {

        String email = binding.etForgotPasswordEmail.getText().toString().trim();
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etForgotPasswordEmail.setError(getString(R.string.enterEmailInvalid));
            return;
        } else {
            binding.etForgotPasswordEmail.setError(null);
        }
        setLoading(true);
        // ASUNCIÓN: El método en AuthControllerApi se llama 'forgotPassword' y devuelve Call<GenericResponseDTOString>
        authControllerApi.forgotPassword(email).enqueue(new Callback<GenericResponseDTOString>() {
            @Override
            public void onResponse(@NonNull Call<GenericResponseDTOString> call, @NonNull Response<GenericResponseDTOString> response) {
                if (!isAdded() || binding == null) return;
                setLoading(false);
                if (!response.isSuccessful()) {
                    String errorMsg = parseErrorMessage(response.errorBody());
                    Log.w("EnterEmailFragment", "Request code failed: " + response.code() + " - " + errorMsg);
                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show(); // Muestra el error parseado
                    return;
                }
                Log.d("EnterEmailFragment", "Request code successful for " + email);
                Toast.makeText(getContext(), getString(R.string.enterEmailCodeSent, email), Toast.LENGTH_LONG).show();
                if (listener != null) {
                    listener.onCodeSent(email);
                }
            }
            @Override
            public void onFailure(@NonNull Call<GenericResponseDTOString> call, @NonNull Throwable t) {
                if (!isAdded() || binding == null) return;
                setLoading(false);
                Log.e("EnterEmailFragment", "Request code network failure", t);
                String networkErrorMsg = "Error de red: " + (t.getMessage() != null ? t.getMessage() : getString(R.string.enterEmailNetworkError));
                Toast.makeText(getContext(), networkErrorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }


    private String parseErrorMessage(ResponseBody errorBody) {
        if (errorBody == null) return "Error desconocido"; // Manejar cuerpo nulo
        try {

            String errorStr = errorBody.string();
            JSONObject json = new JSONObject(errorStr);
            return json.optString("message", "Error inesperado");
        } catch (Exception e) {
            Log.e("ParseError", "No se pudo parsear el error", e);
            return "Error desconocido del servidor";
        }
    }


    private void setLoading(boolean isLoading) {
        if (binding != null) {
            binding.btnSendResetCode.setEnabled(!isLoading);
            binding.btnSendResetCode.setText(isLoading ?
                    getString(R.string.enterEmailSendingCode) :
                    getString(R.string.enterEmailSendCodeBtn));

            binding.btnGoBack.setEnabled(!isLoading);
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
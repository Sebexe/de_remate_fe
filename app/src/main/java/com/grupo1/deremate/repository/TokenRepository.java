package com.grupo1.deremate.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log; // Import Log

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;


import java.io.IOException;
import java.nio.charset.StandardCharsets; // Import Charsets
import java.security.GeneralSecurityException;
import java.security.Key; // Import Key
import java.util.Base64; // Import Base64 for potential secret key decoding

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TokenRepository {
    private static final String TAG = "TokenRepository"; // Tag para logs
    private static final String PREF_FILE_NAME = "encrypted_prefs";
    private static final String KEY_JWT_TOKEN = "jwt_token";
    private final SharedPreferences encryptedPrefs;

    // Opcional: Si necesitas validar la firma (generalmente no en el cliente)
    // Deberías obtener esto de una configuración segura, NO hardcodearlo.
    // private String jwtSecretKey = "tu_super_secreto_del_backend_codificado_en_base64";

    @Inject
    public TokenRepository(Context context) {
        SharedPreferences prefs;
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            prefs = EncryptedSharedPreferences.create(
                    context,
                    PREF_FILE_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            // Considera un manejo de errores más específico si es necesario
            Log.e(TAG, "Error initializing EncryptedSharedPreferences", e);
            throw new RuntimeException("Error initializing EncryptedSharedPreferences", e);
        }
        this.encryptedPrefs = prefs;
    }

    public void saveToken(String token) {
        encryptedPrefs.edit().putString(KEY_JWT_TOKEN, token).apply();
        Log.d(TAG, "Token saved successfully.");
    }

    public String getToken() {
        String token = encryptedPrefs.getString(KEY_JWT_TOKEN, null);
        // Log.d(TAG, "Retrieved token: " + (token != null ? "Exists" : "NULL")); // Evita loguear el token completo
        return token;
    }

    public void clearToken() {
        encryptedPrefs.edit().remove(KEY_JWT_TOKEN).apply();
        Log.d(TAG, "Token cleared.");
    }

    // --- NUEVO MÉTODO PARA EXTRAER USER ID ---
    /**
     * Decodifica el token JWT almacenado y extrae el claim "userId".
     * ¡OJO! Este método generalmente NO valida la firma del token,
     * solo parsea el contenido. La validación de firma completa
     * usualmente se hace en el backend.
     *
     * @return El ID del usuario como Long, o null si el token no existe,
     * es inválido, o no contiene el claim "userId".
     */
    public Long getUserIdFromToken() {
        String token = getToken();
        if (token == null || token.isEmpty()) {
            Log.w(TAG, "Attempted to get userId, but token is null or empty.");
            return null;
        }

        try {
            // Parsear el token sin validar la firma (más común en cliente)
            // Necesitamos encontrar el último punto para separar el payload sin firma
            int lastDot = token.lastIndexOf('.');
            String tokenWithoutSignature = (lastDot > 0) ? token.substring(0, lastDot + 1) : token;

            // Jwts.parserBuilder() requiere la firma para parseClaimsJws,
            // pero podemos parsear sin verificarla si solo queremos los claims.
            // Sin embargo, es más simple obtener el payload directamente.

            String[] splitToken = token.split("\\.");
            if (splitToken.length < 2) { // Necesita al menos header y payload
                Log.e(TAG, "Invalid JWT format (less than 2 parts).");
                return null;
            }
            String base64EncodedPayload = splitToken[1];
            // Usar Base64 de Android para decodificar Base64Url
            byte[] decodedPayloadBytes = android.util.Base64.decode(
                    base64EncodedPayload,
                    android.util.Base64.URL_SAFE | android.util.Base64.NO_PADDING | android.util.Base64.NO_WRAP
            );
            String payloadJson = new String(decodedPayloadBytes, StandardCharsets.UTF_8);

            // Usar Gson (o tu librería JSON) para parsear el payload
            com.google.gson.JsonObject payload = com.google.gson.JsonParser.parseString(payloadJson).getAsJsonObject();

            if (payload.has("userId")) {
                // Asume que userId es un número (Long o Integer)
                long userId = payload.get("userId").getAsLong();
                Log.d(TAG, "Extracted userId from token: " + userId);
                return userId;
            } else {
                Log.w(TAG, "Token payload does not contain 'userId' claim.");
                return null;
            }

        } catch (com.google.gson.JsonSyntaxException e) {
            Log.e(TAG, "Error parsing JWT payload JSON", e);
            return null;
        } catch (IllegalArgumentException e) {
            // Error en decodificación Base64
            Log.e(TAG, "Error decoding Base64 payload", e);
            return null;
        } catch (Exception e) {
            // Captura genérica para otros posibles errores inesperados
            Log.e(TAG, "Unexpected error parsing JWT payload", e);
            return null;
        }


    }
}
package com.grupo1.deremate.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.zxing.integration.android.IntentIntegrator
import com.grupo1.deremate.databinding.FragmentScannerBinding
import com.grupo1.deremate.infrastructure.ApiClient
import com.grupo1.deremate.models.DeliveryDTO
import com.grupo1.deremate.apis.DeliveryControllerApi
import com.grupo1.deremate.DeliveryDetailActivity
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@AndroidEntryPoint
class ScannerFragment : Fragment() {

    private var _binding: FragmentScannerBinding? = null
    private val binding get() = _binding!!

    @Inject lateinit var apiClient: ApiClient
    private lateinit var deliveryApi: DeliveryControllerApi

    private val qrScannerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val intentResult = IntentIntegrator.parseActivityResult(result.resultCode, result.data)
        if (intentResult != null && intentResult.contents != null) {
            val deliveryId = intentResult.contents.toLongOrNull()
            if (deliveryId != null) fetchDelivery(deliveryId)
            else Toast.makeText(requireContext(), "QR inválido", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentScannerBinding.inflate(inflater, container, false)
        deliveryApi = apiClient.createService(DeliveryControllerApi::class.java)
        startQRScanner()
        return binding.root
    }

    private fun startQRScanner() {
        val integrator = IntentIntegrator.forSupportFragment(this)
        integrator.setPrompt("Escanea el código QR")
        integrator.setOrientationLocked(false)
        integrator.setBeepEnabled(true)
        integrator.captureActivity = CaptureActivityPortrait::class.java
        qrScannerLauncher.launch(integrator.createScanIntent())
    }

    private fun fetchDelivery(id: Long) {
        deliveryApi.getDeliveryById(id).enqueue(object : Callback<DeliveryDTO> {
            override fun onResponse(call: Call<DeliveryDTO>, response: Response<DeliveryDTO>) {
                if (response.isSuccessful && response.body() != null) {
                    val intent = Intent(requireContext(), DeliveryDetailActivity::class.java)
                    intent.putExtra("delivery", response.body())
                    startActivity(intent)
                } else {
                    Toast.makeText(requireContext(), "Delivery no encontrado", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<DeliveryDTO>, t: Throwable) {
                Log.e("ScannerFragment", "Error al consultar delivery", t)
                Toast.makeText(requireContext(), "Error al conectar con el servidor", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

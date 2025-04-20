package com.grupo1.deremate

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.grupo1.deremate.apis.RouteControllerApi
import com.grupo1.deremate.databinding.ActivityDeliveryDetailBinding
import com.grupo1.deremate.infrastructure.ApiClient
import com.grupo1.deremate.models.DeliveryDTO
import com.grupo1.deremate.models.RouteDTO
import com.grupo1.deremate.repository.UserRepository
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@AndroidEntryPoint
class DeliveryDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDeliveryDetailBinding

    private lateinit var routeApi: RouteControllerApi;

    private lateinit var delivery: DeliveryDTO;

    @Inject
    lateinit var userRepository: UserRepository;

    @Inject
    lateinit var apiClient: ApiClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeliveryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val _delivery = intent.getSerializableExtra("delivery") as? DeliveryDTO
        _delivery?.let { showDeliveryInfo(it) }
        if (_delivery != null) {
            delivery = _delivery
        }


        routeApi = apiClient.createService(RouteControllerApi::class.java)
        setupBtn()
    }

    private fun setupBtn() {
        binding.btnStartDelivery.setOnClickListener {
            routeApi.assignRouteToUser(delivery.route?.id, userRepository.user.id).enqueue(object : Callback<RouteDTO> {
                override fun onResponse(
                    call: Call<RouteDTO?>,
                    response: Response<RouteDTO?>
                ) {

                }

                override fun onFailure(
                    call: Call<RouteDTO?>,
                    t: Throwable
                ) {
                }

            })
        }
    }

    private fun showDeliveryInfo(delivery: DeliveryDTO) {
        binding.tvDeliveryId.text = "ID: ${delivery.id}"
        binding.tvStatus.text = "Estado: ${delivery.status?.value}"
        binding.tvDestination.text = "Destino: ${delivery.destination}"
    }
}

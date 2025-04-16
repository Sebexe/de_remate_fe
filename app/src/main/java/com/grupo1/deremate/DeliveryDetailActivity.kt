package com.grupo1.deremate

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.grupo1.deremate.databinding.ActivityDeliveryDetailBinding
import com.grupo1.deremate.models.DeliveryDTO

class DeliveryDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDeliveryDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeliveryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val delivery = intent.getSerializableExtra("delivery") as? DeliveryDTO
        delivery?.let { showDeliveryInfo(it) }
    }

    private fun showDeliveryInfo(delivery: DeliveryDTO) {
        binding.tvDeliveryId.text = "ID: ${delivery.id}"
        binding.tvStatus.text = "Estado: ${delivery.status?.value}"
        binding.tvDestination.text = "Destino: ${delivery.destination}"
    }
}

package com.grupo1.deremate.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.grupo1.deremate.R
import com.grupo1.deremate.models.DeliveryDTO

class PackageDetailFragment(private val delivery: DeliveryDTO) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = android.app.AlertDialog.Builder(requireContext())
        val inflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.dialog_package_detail, null)

        view.findViewById<TextView>(R.id.tvPackageId).text = "Código del Paquete: ${delivery.id}"
        view.findViewById<TextView>(R.id.tvStatus).text = "Estado de Entrega: ${delivery.status}"
        view.findViewById<TextView>(R.id.tvLocation).text = "Ubicación en Depósito: ${delivery.packageLocation}"
        view.findViewById<TextView>(R.id.tvQrCode).text = "Código QR: ${delivery.qrCode ?: "No disponible"}"
        view.findViewById<TextView>(R.id.tvPin).text = "PIN de Verificación: ${delivery.pin ?: "No disponible"}"
        view.findViewById<TextView>(R.id.tvCreatedDate).text = "Fecha de Creación: ${delivery.createdDate ?: "No disponible"}"

        builder.setView(view)
            .setPositiveButton("Cerrar") { dialog, _ -> dialog.dismiss() }

        return builder.create()
    }
}

package com.grupo1.deremate.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.grupo1.deremate.R
import com.grupo1.deremate.models.PackageDTO

class PackageDetailFragment(private val packageDTO: PackageDTO) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view: View = LayoutInflater.from(context).inflate(R.layout.dialog_package_detail, null)

        // Setea los textos con datos del paquete
        view.findViewById<TextView>(R.id.tvPackageId).text =
            getString(R.string.package_id_format, packageDTO.id.toString())

        view.findViewById<TextView>(R.id.tvStatus).text =
            getString(R.string.package_status_format, packageDTO.status ?: "No disponible")

        view.findViewById<TextView>(R.id.tvLocation).text =
            getString(R.string.package_location_format, packageDTO.packageLocation ?: "No disponible")

        view.findViewById<TextView>(R.id.tvCreatedDate)?.text =
            getString(R.string.package_created_format, packageDTO.createdDate ?: "No disponible")

        return AlertDialog.Builder(requireContext())
            .setView(view)
            .setPositiveButton("Cerrar") { dialog, _ -> dialog.dismiss() }
            .create()
    }
}

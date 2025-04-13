package com.grupo1.deremate.infrastructure

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.grupo1.deremate.databinding.ItemPackageBinding
import com.grupo1.deremate.models.PackageDTO

class PackageAdapter(private val packages: List<PackageDTO>) :
    RecyclerView.Adapter<PackageAdapter.PackageViewHolder>() {

    inner class PackageViewHolder(private val binding: ItemPackageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(pkg: PackageDTO) {
            binding.tvPackageId.text = "Código: ${pkg.id}"
            binding.tvPackageLocation.text = "Ubicación: ${pkg.packageLocation}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackageViewHolder {
        val binding = ItemPackageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PackageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PackageViewHolder, position: Int) {
        holder.bind(packages[position])
    }

    override fun getItemCount(): Int = packages.size
}

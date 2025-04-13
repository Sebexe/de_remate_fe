package com.grupo1.deremate.infrastructure

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.grupo1.deremate.R
import com.grupo1.deremate.databinding.ItemPackageBinding
import com.grupo1.deremate.fragments.PackageDetailFragment
import com.grupo1.deremate.models.PackageDTO

class PackageAdapter(
    private val context: Context,
    private val fragment: Fragment,
    private val packages: List<PackageDTO>
) : RecyclerView.Adapter<PackageAdapter.PackageViewHolder>() {

    inner class PackageViewHolder(private val binding: ItemPackageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(pkg: PackageDTO) {
            val fragmentManager = fragment.requireActivity().supportFragmentManager
            binding.tvPackageId.text = context.getString(R.string.package_code_format, pkg.id)
            binding.tvPackageLocation.text = context.getString(R.string.package_location_format, pkg.packageLocation)

            binding.root.setOnClickListener {
                val detailFragment = PackageDetailFragment(pkg)
                detailFragment.show(fragmentManager, "packageDetail")
            }
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

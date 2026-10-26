package com.example.yansfood.ui.menu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.yansfood.R
import com.example.yansfood.data.model.Product
import com.example.yansfood.databinding.ItemProductBinding
import com.example.yansfood.util.toRupiahFormat

class ProductAdapter(
    private var productList: List<Product> = emptyList(),
    private val onStockToggle: (Product, Boolean) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.tvProductName.text = product.name
            binding.tvProductCategory.text = "Kategori: ${product.category}"
            binding.tvProductPriceAndStock.text = "${product.price.toRupiahFormat()} • ${product.stock} porsi"

            // Tampilkan foto produk dengan Coil
            binding.ivProductImage.load(product.imageUrl) {
                crossfade(true)
                placeholder(R.drawable.placeholder_food)
                error(R.drawable.placeholder_food)
                transformations(RoundedCornersTransformation(12f))
            }

            // Bind status stok ke Switch & Badge
            updateStockUI(product.isAvailable)

            // Remove listener before setting checked to avoid trigger loops
            binding.switchStock.setOnCheckedChangeListener(null)
            binding.switchStock.isChecked = product.isAvailable

            binding.switchStock.setOnCheckedChangeListener { _, isChecked ->
                product.isAvailable = isChecked
                updateStockUI(isChecked)
                onStockToggle(product, isChecked)
            }
        }

        private fun updateStockUI(isAvailable: Boolean) {
            val context = binding.root.context
            if (isAvailable) {
                binding.tvStockBadge.text = "Tersedia"
                binding.tvStockBadge.setBackgroundResource(R.drawable.bg_badge_success)
                binding.tvStockBadge.setTextColor(context.getColor(R.color.status_success))
            } else {
                binding.tvStockBadge.text = "Habis"
                binding.tvStockBadge.setBackgroundResource(R.drawable.bg_badge_danger)
                binding.tvStockBadge.setTextColor(context.getColor(R.color.status_danger))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(productList[position])
    }

    override fun getItemCount(): Int = productList.size

    fun updateData(newProducts: List<Product>) {
        this.productList = newProducts
        notifyDataSetChanged()
    }
}

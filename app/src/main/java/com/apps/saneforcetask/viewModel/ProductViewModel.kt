package com.apps.saneforcetask.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apps.saneforcetask.apiRepo.ApiRepo
import com.apps.saneforcetask.model.Product
import com.apps.saneforcetask.viewState.ProductViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class ProductViewModel: ViewModel() {
    private val _productState = MutableStateFlow<ProductViewState>(ProductViewState.Loading)
    val productState: StateFlow<ProductViewState> = _productState.asStateFlow()

    fun fetchProducts(context: Context) {
        viewModelScope.launch(Dispatchers.Main) {
            val productList = ApiRepo.getProducts(context = context)
            if (productList != null) {
                val products = productList.objectArray().mapNotNull { product ->
                    Product.parse(product)
                }.toList()
                if (products.isEmpty()) {
                    _productState.update { ProductViewState.Empty(emptyMsg = "No items found") }
                } else {
                    _productState.update { ProductViewState.ShowProducts(products = products) }
                }
            }
            else {
                _productState.update { ProductViewState.Error(errorMsg = "Something went wrong") }
            }
        }
    }

    fun deleteProduct(productCode: String, products: List<Product>) {
        viewModelScope.launch(Dispatchers.Main) {
            val updatedProducts = products.filter { it.productCode != productCode }
            _productState.update { ProductViewState.ShowProducts(updatedProducts) }
        }
    }

    fun getProductCount(productCode: String, isAdd: Boolean, products: List<Product>) {
        viewModelScope.launch(Dispatchers.IO) {
            val mutableProducts = products.toMutableList()
            val index = mutableProducts.indexOfFirst { it.productCode == productCode }
            if (index != -1) {
                val product = mutableProducts[index]
                val maxQty = product.convQty.toIntOrNull() ?: 0
                var quantity = product.selectedQty

                quantity = when {
                    isAdd && quantity < maxQty -> quantity + 1
                    !isAdd && quantity > 0 -> quantity - 1
                    else -> quantity // No change if limits are reached
                }

                val updatedProduct = product.copy(
                    selectedQty = quantity,
                    productAmount = quantity * 1730
                )
                mutableProducts[index] = updatedProduct
                withContext(Dispatchers.Main) {
                    _productState.update { ProductViewState.ShowProducts(mutableProducts) }
                }
            } else {
                _productState.update { ProductViewState.Error("Product not found") }
            }
        }
    }


    fun updateProductList(context: Context, products: List<Product>, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.Main) {
            //_productState.update { ProductViewState.Loading }
            if (products.isNotEmpty()) {
                val productsArray = JSONArray()
                products.forEach { product ->
                    productsArray.put(JSONObject().apply {
                        put("product_code", product.productCode)
                        put("product_name", product.productName)
                        put("Product_Qty", product.selectedQty)
                        put("product_code", product.productCode)
                        put("Rate", 1730)   // I didn't get any rate in GET Api. So manually setting the rate
                        put("Product_Amount", product.productAmount)
                    })
                }
                val productList = ApiRepo.updateProducts(
                    context = context,
                    json = JSONObject().apply { put("data", productsArray) }
                )
                if (productList != null) {
                    onSuccess()
                }
                else {
                    _productState.update { ProductViewState.Error("Something went wrong") }
                }
            }
            else {
                _productState.update { ProductViewState.Empty(emptyMsg = "No items found") }
            }
        }
    }

    fun JSONArray.objectArray(): Array<JSONObject> = (0 until this.length()).map {
        this.getJSONObject(it)
    }.toTypedArray()
}
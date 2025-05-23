package com.apps.saneforcetask.viewState

import com.apps.saneforcetask.model.Product

sealed class ProductViewState {
    object Loading: ProductViewState()
    data class Empty(val emptyMsg: String): ProductViewState()
    data class ShowProducts(val products: List<Product>): ProductViewState()
    data class Error(val errorMsg: String): ProductViewState()
}
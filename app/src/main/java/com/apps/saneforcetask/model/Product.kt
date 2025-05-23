package com.apps.saneforcetask.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.json.JSONObject

@Serializable
data class Product(
    @SerialName("product_code") val productCode: String,
    @SerialName("product_name") val productName: String,
    @SerialName("product_unit") val productUnit: String,
    val convQty: String,
    val productAmount: Int = 0,
    val selectedQty: Int = 0,
) {
    companion object {
        fun parse(json: JSONObject): Product? = try {
            Json.decodeFromString<Product>(json.toString())
        } catch (e: Exception) {
            null
        }
    }
}
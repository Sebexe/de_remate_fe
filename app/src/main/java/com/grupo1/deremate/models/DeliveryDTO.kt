/**
 *
 * Please note:
 * This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * Do not edit this file manually.
 *
 */

@file:Suppress(
    "ArrayInDataClass",
    "EnumEntryName",
    "RemoveRedundantQualifierName",
    "UnusedImport"
)

package com.grupo1.deremate.models

import com.grupo1.deremate.models.ProductDTO
import com.grupo1.deremate.models.RouteDTO

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * 
 *
 * @param id 
 * @param status 
 * @param destination 
 * @param packageLocation 
 * @param createdDate 
 * @param deliveryStartDate 
 * @param deliveryEndDate 
 * @param route 
 * @param products 
 * @param qrCode 
 * @param pin 
 */


data class DeliveryDTO (

    @SerializedName("id")
    val id: kotlin.Long? = null,

    @SerializedName("status")
    val status: DeliveryDTO.Status? = null,

    @SerializedName("destination")
    val destination: kotlin.String? = null,

    @SerializedName("packageLocation")
    val packageLocation: kotlin.String? = null,

    @SerializedName("createdDate")
    val createdDate: String? = null,

    @SerializedName("deliveryStartDate")
    val deliveryStartDate: String? = null,

    @SerializedName("deliveryEndDate")
    val deliveryEndDate: String? = null,

    @SerializedName("route")
    val route: RouteDTO? = null,

    @SerializedName("products")
    val products: kotlin.collections.List<ProductDTO>? = null,

    @SerializedName("qrCode")
    val qrCode: kotlin.String? = null,

    @SerializedName("pin")
    val pin: kotlin.String? = null

): Serializable {

    /**
     * 
     *
     * Values: NOT_DELIVERED,DELIVERED,REJECTED
     */
    enum class Status(val value: kotlin.String) {
        @SerializedName(value = "NOT_DELIVERED") NOT_DELIVERED("NOT_DELIVERED"),
        @SerializedName(value = "DELIVERED") DELIVERED("DELIVERED"),
        @SerializedName(value = "REJECTED") REJECTED("REJECTED");
    }

}


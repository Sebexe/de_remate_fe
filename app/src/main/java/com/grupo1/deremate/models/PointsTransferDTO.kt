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


import com.google.gson.annotations.SerializedName

/**
 * 
 *
 * @param fromUserId 
 * @param toUserId 
 * @param amount 
 */


data class PointsTransferDTO (

    @SerializedName("fromUserId")
    val fromUserId: kotlin.Long? = null,

    @SerializedName("toUserId")
    val toUserId: kotlin.Long? = null,

    @SerializedName("amount")
    val amount: kotlin.Int? = null

) {


}


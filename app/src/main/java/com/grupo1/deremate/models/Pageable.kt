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
 * @param page 
 * @param propertySize 
 * @param sort 
 */


data class Pageable (

    @SerializedName("page")
    val page: kotlin.Int? = null,

    @SerializedName("size")
    val propertySize: kotlin.Int? = null,

    @SerializedName("sort")
    val sort: kotlin.collections.List<kotlin.String>? = null

) {


}


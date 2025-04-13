package com.grupo1.deremate.apis

import com.grupo1.deremate.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Call
import okhttp3.RequestBody
import com.google.gson.annotations.SerializedName

import com.grupo1.deremate.models.CreateDeliveryDTO
import com.grupo1.deremate.models.DeliveryDTO
import com.grupo1.deremate.models.PackageDTO

interface DeliveryControllerApi {
    /**
     * PUT api/v1/delivery/{id}/cancel
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @return [Call]<[kotlin.Any]>
     */
    @PUT("api/v1/delivery/{id}/cancel")
    fun cancelDelivery(@Path("id") id: kotlin.Long): Call<kotlin.Any>

    /**
     * PUT api/v1/delivery/{id}/confirm
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @param pin 
     * @return [Call]<[kotlin.Any]>
     */
    @PUT("api/v1/delivery/{id}/confirm")
    fun confirmDelivery(@Path("id") id: kotlin.Long, @Query("pin") pin: kotlin.String): Call<kotlin.Any>

    /**
     * POST api/v1/delivery
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param createDeliveryDTO 
     * @return [Call]<[DeliveryDTO]>
     */
    @POST("api/v1/delivery")
    fun createDelivery(@Body createDeliveryDTO: CreateDeliveryDTO): Call<DeliveryDTO>

    /**
     * GET api/v1/delivery/{id}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @return [Call]<[DeliveryDTO]>
     */
    @GET("api/v1/delivery/{id}")
    fun getDeliveryById(@Path("id") id: kotlin.Long): Call<DeliveryDTO>

    @GET("/api/v1/delivery/warehouse")
    fun getPackagesInWarehouse(): Call<List<PackageDTO>>


}

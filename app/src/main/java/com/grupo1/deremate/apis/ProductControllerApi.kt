package com.grupo1.deremate.apis

import com.grupo1.deremate.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Call
import okhttp3.RequestBody
import com.google.gson.annotations.SerializedName

import com.grupo1.deremate.models.GenericResponseDTO
import com.grupo1.deremate.models.GenericResponseDTOListProductDTO
import com.grupo1.deremate.models.GenericResponseDTOProductDTO
import com.grupo1.deremate.models.ProductDTO

interface ProductControllerApi {
    /**
     * POST api/v1/products
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param productDTO 
     * @return [Call]<[GenericResponseDTOProductDTO]>
     */
    @POST("api/v1/products")
    fun createProduct(@Body productDTO: ProductDTO): Call<GenericResponseDTOProductDTO>

    /**
     * DELETE api/v1/products/{id}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @return [Call]<[GenericResponseDTO]>
     */
    @DELETE("api/v1/products/{id}")
    fun deleteProduct(@Path("id") id: kotlin.Long): Call<GenericResponseDTO>

    /**
     * GET api/v1/products
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @return [Call]<[GenericResponseDTOListProductDTO]>
     */
    @GET("api/v1/products")
    fun getAllProducts(): Call<GenericResponseDTOListProductDTO>

    /**
     * GET api/v1/products/{id}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @return [Call]<[GenericResponseDTOProductDTO]>
     */
    @GET("api/v1/products/{id}")
    fun getProduct(@Path("id") id: kotlin.Long): Call<GenericResponseDTOProductDTO>

    /**
     * PUT api/v1/products/{id}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @param productDTO 
     * @return [Call]<[GenericResponseDTO]>
     */
    @PUT("api/v1/products/{id}")
    fun updateProduct(@Path("id") id: kotlin.Long, @Body productDTO: ProductDTO): Call<GenericResponseDTO>

}

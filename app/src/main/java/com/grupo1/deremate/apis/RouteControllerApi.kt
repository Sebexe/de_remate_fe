package com.grupo1.deremate.apis

import retrofit2.http.*
import retrofit2.Call
import com.google.gson.annotations.SerializedName

import com.grupo1.deremate.models.AvailableRouteDTO
import com.grupo1.deremate.models.CreateRouteDTO
import com.grupo1.deremate.models.RouteDTO

interface RouteControllerApi {
    /**
     * PUT api/v1/routes/{routeId}/assign
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param routeId 
     * @param userId 
     * @return [Call]<[RouteDTO]>
     */
    @PUT("api/v1/routes/{routeId}/assign")
    fun assignRouteToUser(@Path("routeId") routeId: Long?, @Query("userId") userId: Long?): Call<RouteDTO>

    /**
     * PUT api/v1/routes/{routeId}/complete
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param routeId 
     * @return [Call]<[RouteDTO]>
     */
    @PUT("api/v1/routes/{routeId}/complete")
    fun completeRoute(@Path("routeId") routeId: kotlin.Long): Call<RouteDTO>

    /**
     * POST api/v1/routes
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param createRouteDTO 
     * @return [Call]<[RouteDTO]>
     */
    @POST("api/v1/routes")
    fun createRoute(@Body createRouteDTO: CreateRouteDTO): Call<RouteDTO>

    /**
     * GET api/v1/routes
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @return [Call]<[kotlin.collections.List<RouteDTO>]>
     */
    @GET("api/v1/routes")
    fun getAllRoutes(): Call<kotlin.collections.List<RouteDTO>>

    /**
     * GET api/v1/routes/available
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param originBarrio  (optional)
     * @param destinationBarrio  (optional)
     * @return [Call]<[kotlin.collections.List<AvailableRouteDTO>]>
     */
    @GET("api/v1/routes/available")
    fun getAvailableRoutes(@Query("originBarrio") originBarrio: kotlin.String? = null, @Query("destinationBarrio") destinationBarrio: kotlin.String? = null): Call<kotlin.collections.List<AvailableRouteDTO>>

    /**
     * GET api/v1/routes/user/{userId}/completed
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param userId 
     * @return [Call]<[kotlin.collections.List<RouteDTO>]>
     */
    @GET("api/v1/routes/user/{userId}/completed")
    fun getCompletedRoutesByUser(@Path("userId") userId: kotlin.Long): Call<kotlin.collections.List<RouteDTO>>


    /**
    * enum for parameter status
    */
    enum class StatusGetRoutesByUser(val value: kotlin.String) {
        @SerializedName(value = "PENDING") PENDING("PENDING"),
        @SerializedName(value = "INITIATED") INITIATED("INITIATED"),
        @SerializedName(value = "COMPLETED") COMPLETED("COMPLETED")
    }

    /**
     * GET api/v1/routes/user/{userId}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param userId 
     * @param status  (optional)
     * @return [Call]<[kotlin.collections.List<RouteDTO>]>
     */
    @GET("api/v1/routes/user/{userId}")
    fun getRoutesByUser(@Path("userId") userId: kotlin.Long, @Query("status") status: StatusGetRoutesByUser? = null): Call<kotlin.collections.List<RouteDTO>>

}

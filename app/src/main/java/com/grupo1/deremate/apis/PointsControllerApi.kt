package com.grupo1.deremate.apis

import com.grupo1.deremate.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Call
import okhttp3.RequestBody
import com.google.gson.annotations.SerializedName

import com.grupo1.deremate.models.PagePointsDTO
import com.grupo1.deremate.models.Pageable
import com.grupo1.deremate.models.PointsDTO
import com.grupo1.deremate.models.PointsOperationDTO
import com.grupo1.deremate.models.PointsStatisticsDTO
import com.grupo1.deremate.models.PointsTransferDTO

import com.grupo1.deremate.models.*

interface PointsControllerApi {
    /**
     * POST api/v1/points/add
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param pointsDTO 
     * @return [Call]<[kotlin.String]>
     */
    @POST("api/v1/points/add")
    fun addPoints(@Body pointsDTO: PointsDTO): Call<kotlin.String>

    /**
     * POST api/v1/points/{userId}/activity-adjustment
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param userId 
     * @param activityLevel 
     * @return [Call]<[kotlin.String]>
     */
    @POST("api/v1/points/{userId}/activity-adjustment")
    fun adjustPointsByActivity(@Path("userId") userId: kotlin.Long, @Query("activityLevel") activityLevel: kotlin.Int): Call<kotlin.String>

    /**
     * POST api/v1/points/{userId}/penalty
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param userId 
     * @param penalty 
     * @return [Call]<[kotlin.String]>
     */
    @POST("api/v1/points/{userId}/penalty")
    fun applyPointsPenalty(@Path("userId") userId: kotlin.Long, @Query("penalty") penalty: kotlin.Int): Call<kotlin.String>

    /**
     * POST api/v1/points/seasonal-bonus
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param seasonName 
     * @param multiplier 
     * @return [Call]<[kotlin.String]>
     */
    @POST("api/v1/points/seasonal-bonus")
    fun applySeasonalBonus(@Query("seasonName") seasonName: kotlin.String, @Query("multiplier") multiplier: kotlin.Double): Call<kotlin.String>

    /**
     * POST api/v1/points/{userId}/bonus
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param userId 
     * @param bonus 
     * @return [Call]<[kotlin.String]>
     */
    @POST("api/v1/points/{userId}/bonus")
    fun awardBonusPoints(@Path("userId") userId: kotlin.Long, @Query("bonus") bonus: kotlin.Int): Call<kotlin.String>

    /**
     * GET api/v1/points/{userId}/next-tier
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param userId 
     * @return [Call]<[kotlin.Int]>
     */
    @GET("api/v1/points/{userId}/next-tier")
    fun calculatePointsForNextTier(@Path("userId") userId: kotlin.Long): Call<kotlin.Int>

    /**
     * GET api/v1/points/{userId}/tier
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param userId 
     * @return [Call]<[kotlin.Int]>
     */
    @GET("api/v1/points/{userId}/tier")
    fun calculateUserTier(@Path("userId") userId: kotlin.Long): Call<kotlin.Int>

    /**
     * POST api/v1/points/{userId}/convert-to-rewards
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param userId 
     * @param pointsToConvert 
     * @return [Call]<[kotlin.String]>
     */
    @POST("api/v1/points/{userId}/convert-to-rewards")
    fun convertPointsToRewards(@Path("userId") userId: kotlin.Long, @Query("pointsToConvert") pointsToConvert: kotlin.Int): Call<kotlin.String>

    /**
     * PUT api/v1/points/decrease-all
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param percentage 
     * @return [Call]<[kotlin.String]>
     */
    @PUT("api/v1/points/decrease-all")
    fun decreaseAllPointsByPercentage(@Query("percentage") percentage: kotlin.Double): Call<kotlin.String>

    /**
     * PUT api/v1/points/{userId}/divide
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param userId 
     * @param pointsOperationDTO 
     * @return [Call]<[kotlin.String]>
     */
    @PUT("api/v1/points/{userId}/divide")
    fun dividePoints(@Path("userId") userId: kotlin.Long, @Body pointsOperationDTO: PointsOperationDTO): Call<kotlin.String>

    /**
     * POST api/v1/points/expire
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param days 
     * @return [Call]<[kotlin.String]>
     */
    @POST("api/v1/points/expire")
    fun expirePoints(@Query("days") days: kotlin.Int): Call<kotlin.String>

    /**
     * GET api/v1/points/export
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @return [Call]<[kotlin.collections.Map<kotlin.String, PointsDTO>]>
     */
    @GET("api/v1/points/export")
    fun exportAllPointsData(): Call<kotlin.collections.Map<kotlin.String, PointsDTO>>

    /**
     * GET api/v1/points/all
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param page  (optional)
     * @param size  (optional)
     * @param sort  (optional)
     * @return [Call]<[PagePointsDTO]>
     */
    @GET("api/v1/points/all")
    fun getAllUsersWithPoints(@Query("page") page: kotlin.Int? = null, @Query("size") size: kotlin.Int? = null, @Query("sort") sort: kotlin.collections.List<kotlin.String>? = null): Call<PagePointsDTO>

    /**
     * GET api/v1/points/average
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @return [Call]<[kotlin.Double]>
     */
    @GET("api/v1/points/average")
    fun getAveragePointsPerUser(): Call<kotlin.Double>

    /**
     * GET api/v1/points/leaderboard
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param startDate 
     * @param endDate 
     * @param limit  (optional, default to 10)
     * @return [Call]<[kotlin.collections.List<PointsDTO>]>
     */
    @GET("api/v1/points/leaderboard")
    fun getLeaderboardForPeriod(@Query("startDate") startDate: kotlin.String, @Query("endDate") endDate: kotlin.String, @Query("limit") limit: kotlin.Int? = 10): Call<kotlin.collections.List<PointsDTO>>

    /**
     * GET api/v1/points/median
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @return [Call]<[kotlin.Int]>
     */
    @GET("api/v1/points/median")
    fun getMedianPointsValue(): Call<kotlin.Int>

    /**
     * GET api/v1/points/{userId}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param userId 
     * @return [Call]<[PointsDTO]>
     */
    @GET("api/v1/points/{userId}")
    fun getPoints(@Path("userId") userId: kotlin.Long): Call<PointsDTO>

    /**
     * GET api/v1/points/statistics
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @return [Call]<[PointsStatisticsDTO]>
     */
    @GET("api/v1/points/statistics")
    fun getPointsStatistics(): Call<PointsStatisticsDTO>

    /**
     * GET api/v1/points/top
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param limit  (optional, default to 10)
     * @return [Call]<[kotlin.collections.List<PointsDTO>]>
     */
    @GET("api/v1/points/top")
    fun getTopUsers(@Query("limit") limit: kotlin.Int? = 10): Call<kotlin.collections.List<PointsDTO>>

    /**
     * GET api/v1/points/total
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @return [Call]<[kotlin.Int]>
     */
    @GET("api/v1/points/total")
    fun getTotalPoints(): Call<kotlin.Int>

    /**
     * GET api/v1/points/{userId}/history
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param userId 
     * @return [Call]<[kotlin.collections.List<PointsDTO>]>
     */
    @GET("api/v1/points/{userId}/history")
    fun getUserPointsHistory(@Path("userId") userId: kotlin.Long): Call<kotlin.collections.List<PointsDTO>>

    /**
     * GET api/v1/points/above-threshold
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param threshold 
     * @return [Call]<[kotlin.collections.List<PointsDTO>]>
     */
    @GET("api/v1/points/above-threshold")
    fun getUsersAboveThreshold(@Query("threshold") threshold: kotlin.Int): Call<kotlin.collections.List<PointsDTO>>

    /**
     * GET api/v1/points/below-threshold
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param threshold 
     * @return [Call]<[kotlin.collections.List<PointsDTO>]>
     */
    @GET("api/v1/points/below-threshold")
    fun getUsersBelowThreshold(@Query("threshold") threshold: kotlin.Int): Call<kotlin.collections.List<PointsDTO>>

    /**
     * GET api/v1/points/range
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param min 
     * @param max 
     * @return [Call]<[kotlin.collections.List<PointsDTO>]>
     */
    @GET("api/v1/points/range")
    fun getUsersInPointsRange(@Query("min") min: kotlin.Int, @Query("max") max: kotlin.Int): Call<kotlin.collections.List<PointsDTO>>

    /**
     * GET api/v1/points/{userId}/has-points
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param userId 
     * @return [Call]<[kotlin.Boolean]>
     */
    @GET("api/v1/points/{userId}/has-points")
    fun hasPoints(@Path("userId") userId: kotlin.Long): Call<kotlin.Boolean>

    /**
     * POST api/v1/points/import
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param requestBody 
     * @return [Call]<[kotlin.String]>
     */
    @POST("api/v1/points/import")
    fun importPointsData(@Body requestBody: kotlin.collections.Map<kotlin.String, PointsDTO>): Call<kotlin.String>

    /**
     * PUT api/v1/points/increase-all
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param percentage 
     * @return [Call]<[kotlin.String]>
     */
    @PUT("api/v1/points/increase-all")
    fun increaseAllPointsByPercentage(@Query("percentage") percentage: kotlin.Double): Call<kotlin.String>

    /**
     * POST api/v1/points/merge
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param targetUserId 
     * @param sourceUserIds 
     * @return [Call]<[kotlin.String]>
     */
    @POST("api/v1/points/merge")
    fun mergeUserPoints(@Query("targetUserId") targetUserId: kotlin.Long, @Query("sourceUserIds") sourceUserIds: @JvmSuppressWildcards kotlin.collections.List<kotlin.Long>): Call<kotlin.String>

    /**
     * PUT api/v1/points/{userId}/multiply
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param userId 
     * @param pointsOperationDTO 
     * @return [Call]<[kotlin.String]>
     */
    @PUT("api/v1/points/{userId}/multiply")
    fun multiplyPoints(@Path("userId") userId: kotlin.Long, @Body pointsOperationDTO: PointsOperationDTO): Call<kotlin.String>

    /**
     * DELETE api/v1/points/reset-all
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @return [Call]<[kotlin.String]>
     */
    @DELETE("api/v1/points/reset-all")
    fun resetAllPoints(): Call<kotlin.String>

    /**
     * DELETE api/v1/points/{userId}/reset
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param userId 
     * @return [Call]<[kotlin.String]>
     */
    @DELETE("api/v1/points/{userId}/reset")
    fun resetPoints(@Path("userId") userId: kotlin.Long): Call<kotlin.String>

    /**
     * POST api/v1/points/threshold/maximum
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param cap 
     * @return [Call]<[kotlin.String]>
     */
    @POST("api/v1/points/threshold/maximum")
    fun setMaximumPointsCap(@Query("cap") cap: kotlin.Int): Call<kotlin.String>

    /**
     * POST api/v1/points/threshold/minimum
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param threshold 
     * @return [Call]<[kotlin.String]>
     */
    @POST("api/v1/points/threshold/minimum")
    fun setMinimumPointsThreshold(@Query("threshold") threshold: kotlin.Int): Call<kotlin.String>

    /**
     * POST api/v1/points/split
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param sourceUserId 
     * @param targetUserIds 
     * @param distribution 
     * @return [Call]<[kotlin.String]>
     */
    @POST("api/v1/points/split")
    fun splitUserPoints(@Query("sourceUserId") sourceUserId: kotlin.Long, @Query("targetUserIds") targetUserIds: @JvmSuppressWildcards kotlin.collections.List<kotlin.Long>, @Query("distribution") distribution: @JvmSuppressWildcards kotlin.collections.List<kotlin.Double>): Call<kotlin.String>

    /**
     * POST api/v1/points/transfer
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param pointsTransferDTO 
     * @return [Call]<[kotlin.String]>
     */
    @POST("api/v1/points/transfer")
    fun transferPoints(@Body pointsTransferDTO: PointsTransferDTO): Call<kotlin.String>

}

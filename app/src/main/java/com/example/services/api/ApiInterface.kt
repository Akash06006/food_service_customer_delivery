package com.example.services.api

import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
import java.util.HashMap

interface ApiInterface {
    @Headers("Content-Type: application/json")
    @POST("mobile/auth/loginTrial")
    fun callLogin(@Body jsonObject: JsonObject): Call<JsonObject>

    @POST("mobile/auth/useReferral")
    fun userReferralCode(@Body jsonObject: JsonObject): Call<JsonObject>

    /*@POST("login/")
    fun callLogin(@Body jsonObject : JsonObject) : Call<JsonObject>*/
    @Multipart
    @POST("register/")
    fun finishRegistartion(
        @PartMap mHashMap: HashMap<String,
                RequestBody>, @Part image: MultipartBody.Part?
    ): Call<JsonObject>


    @Headers("Content-Type: application/json")
    @POST("checkPhoneNumber/")
    fun checkPhoneExistence(@Body jsonObject: JsonObject): Call<JsonObject>

    @POST("mobile/auth/logout")
    fun callLogout(@Body mJsonObject: JsonObject): Call<JsonObject>

    @POST("resetpassword/")
    fun resetPassword(@Body mJsonObject: JsonObject): Call<JsonObject>

    //@POST("resetpassword/")
    //fun getProfile(@Body mJsonObject : JsonObject) : Call<JsonObject>
    @POST("users/changepassword/")
    fun chnagePassword(@Body mJsonObject: JsonObject): Call<JsonObject>

    @GET("mobile/profile/getprofile")
    fun getProfile(): Call<JsonObject>

    @GET("driver/vehicle/latLongList")
    fun getVehicleList(): Call<JsonObject>

    @GET("service/driver/getServiceList")
    fun getServicesList(@Query("status") status: String): Call<JsonObject>

    @GET("fuel/driver/getFuelList")
    fun getFuelEntryList(): Call<JsonObject>

    @GET("notification/driver/getList")
    fun getNotificationList1(): Call<JsonObject>


    @DELETE("mobile/address/delete")
    fun deleteAddress(@Query("addressId") addressId: String): Call<JsonObject>

    @GET("vendor/getVendorList")
    fun getVendorList(): Call<JsonObject>

    @GET("mobile/services/getParentCategories")
    fun getCategories(): Call<JsonObject>

    @GET("mobile/services/home")
    fun getlandingResponse(
        @Query("deliveryType") deliveryType: String, @Query("lat") lat: String, @Query("lng") lng: String, @Query(
            "itemType"
        ) itemType: String
    ): Call<JsonObject>

    @GET("mobile/services/homeVendor")
    fun getHomeVenderResponse(
        @Query("deliveryType") deliveryType: String, @Query("lat") lat: String, @Query("lng") lng: String, @Query(
            "itemType"
        ) itemType: String
    ): Call<JsonObject>


    @DELETE("mobile/cart/clear")
    fun clearCart(): Call<JsonObject>

    @GET("mobile/services/getSubcat")
    fun getSubServices(@Query("category") id: String, @Query("itemType") itemType: String): Call<JsonObject>

    @GET("mobile/services/getServices")//{id}/{itemType}
    fun getServices(@Query("category") id: String, @Query("itemType") itemType: String): Call<JsonObject>

    @POST("job/driver/changeJobStatus")
    fun startCompleteJob(@Body mJsonObject: JsonObject): Call<JsonObject>

    @POST("mobile/address/add")
    fun addAddress(@Body mJsonObject: JsonObject): Call<JsonObject>

    @POST("mobile/rating/addStaffRating")
    fun addDriverRating(@Body mJsonObject: JsonObject): Call<JsonObject>

    @POST("mobile/rating/addCompanyRating")
    fun addCompanyRating(@Body mJsonObject: JsonObject): Call<JsonObject>

    @POST("/api/mobile/services/search")
    fun searchResult(@Body mJsonObject: JsonObject): Call<JsonObject>

    @PUT("mobile/address/update")
    fun updateAddress(@Body mJsonObject: JsonObject): Call<JsonObject>

    @GET("mobile/address/list")
    fun getAddressList(): Call<JsonObject>

    @GET("mobile/cart/list")
    fun cartList(/*@Path("id") id : String*/): Call<JsonObject>

    @GET("mobile/favourite/list")
    fun favList(/*@Path("id") id : String*/): Call<JsonObject>

    @GET("mobile/rating/serviceRatings")
    fun ratingRaviewsList(
        @Query("serviceId") serviceId: String, @Query("page") page: String, @Query(
            "limit"
        ) limit: String
    ): Call<JsonObject>

    @GET("mobile/company/getCompanies")
    fun vendorList(
        @Query("deliveryType") serviceId: String, @Query("page") page: String, @Query("limit") limit: String, @Query(
            "lat"
        ) lat: String, @Query("lng") lng: String, @Query("itemType") itemType: String, @Query("discount") discount: String
    ): Call<JsonObject>


    @GET("mobile/orders/detail/{id}")
    fun orderDetail(@Path("id") id: String): Call<JsonObject>


    /* @POST("mobile/rating/addRating")
    fun addRatings(@Body mJsonObject: RatingReviewListInput): Call<JsonObject>*/


    @Multipart
    @POST("mobile/rating/addRating")
    fun addRatings(
        @PartMap partMap: HashMap<String, RequestBody>?, @Part imagesParts: Array<MultipartBody.Part?>?/*,
        @PartMap contributorsMap: HashMap<String, String>?*/
    ): Call<JsonObject>

    @Multipart
    @POST("mobile/company/gallery/add")
    fun addImages(
        @PartMap partMap: HashMap<String, RequestBody>?, @Part imagesParts: Array<MultipartBody.Part?>?
    ): Call<JsonObject>

    @Multipart
    @POST("mobile/profile/updateprofile")
    fun callUpdateProfile(
        @PartMap mHashMap: HashMap<String,
                RequestBody>, @Part image: MultipartBody.Part?
    ): Call<JsonObject>

    /* @Multipart
     @POST("service/updateServiceEntry")
     fun callUpdateService(
         @PartMap mHashMap: HashMap<String,
                 RequestBody>, @Part image: MultipartBody.Part?
     ): Call<JsonObject>*/
///api/mobile/rating/addRating
    //    {id}
//service_id
    @Multipart
    @POST("fuel/addFuel")
    fun callAddFuelEntry(
        @PartMap mHashMap: HashMap<String,
                RequestBody>, @Part image: MultipartBody.Part?
    ): Call<JsonObject>

    @Multipart
    @POST("service/updateServiceEntry")
    fun callUpdateService(
        @PartMap mHashMap: HashMap<String,
                RequestBody>, @Part image: MultipartBody.Part?
    ): Call<JsonObject>

    @POST("mobile/services/getsubcategories")
    fun getSubCatList(@Body mJsonObject: JsonObject): Call<JsonObject>


    @POST("mobile/cart/add")
    fun addCart(@Body mJsonObject: JsonObject): Call<JsonObject>

    @PUT("mobile/cart/update")
    fun updateCart(@Body mJsonObject: JsonObject): Call<JsonObject>

    @DELETE("mobile/cart/remove")
    fun removeCart(@Query("cartId") cartId: String): Call<JsonObject>

    @POST("mobile/favourite/add")
    fun addFav(@Body mJsonObject: JsonObject): Call<JsonObject>

    @DELETE("mobile/favourite/remove")
    fun removeFav(@Query("favId") favId: String): Call<JsonObject>

    @GET("mobile/schedule/getSchedule")
    fun getTimeSlots(@Query("serviceDate") serviceDate: String): Call<JsonObject>

    @GET("mobile/services/getDates")
    fun getDateSlots(): Call<JsonObject>

    @GET("mobile/coupan/getPromoList")
    fun getPromoList(): Call<JsonObject>

    @POST("mobile/coupan/applyCoupan")
    fun applyCoupon(@Body mJsonObject: JsonObject): Call<JsonObject>

    @POST("mobile/coupan/removeCoupan")
    fun removeCoupon(@Body mJsonObject: JsonObject): Call<JsonObject>

    @Multipart
    @POST("mobile/orders/create")
    fun ordePlace(@PartMap mJsonObject: HashMap<String, RequestBody>, @Part audio: MultipartBody.Part?): Call<JsonObject>


//    @Multipart
//    @POST("mobile/orders/create")
//    fun ordePlace(): Call<JsonObject>


    @POST("mobile/orders/paymentStatus")
    fun updatePaymentSuccess(@Body mJsonObject: JsonObject): Call<JsonObject>

    @POST("mobile/company/shipmentCharges")
    fun checkDeliveryAddress(@Body mJsonObject: JsonObject): Call<JsonObject>


    @POST("mobile/orders/cancel")
    fun cancelOrder(@Body mJsonObject: JsonObject): Call<JsonObject>

    @POST("mobile/orders/status")
    fun completeOrder(@Body mJsonObject: JsonObject): Call<JsonObject>

    @POST("mobile/orders/reorder")
    fun reOrder(@Body mJsonObject: JsonObject): Call<JsonObject>

    @GET("mobile/services/detail")
    fun getServiceDetail(@Query("serviceId") addressId: String): Call<JsonObject>

    @GET("mobile/orders/instructions")
    fun delivetInstuctions(@Query("companyId") companyId: String, @Query("deliveryType") deliveryType: String): Call<JsonObject>

    @GET("mobile/orders/list")
    fun orderList(@Query("progressStatus") progressStatus: String): Call<JsonObject>

    @GET("mobile/orders/list")
    fun orderHistroyList(@Query("progressStatus") progressStatus: String): Call<JsonObject>

    @POST("mobile/profile/updateDatesInfo")
    fun updateDates(@Body mJsonObject: JsonObject): Call<JsonObject>

    @POST("mobile/contactus")
    fun contactUs(@Body mJsonObject: JsonObject): Call<JsonObject>

    @GET("mobile/notification")
    fun getNotificationList(): Call<JsonObject>

//    @FormUrlEncoded
//    @HTTP(method = "DELETE", path = "/mobile/notification/clearAll", hasBody = false)

    @DELETE("mobile/notification/clearAll")
    fun clearAllNotification(): Call<JsonObject>


    @GET("mobile/document")
    fun getLinks(): Call<JsonObject>

    @GET("mobile/getFaq")
    fun getFAQList(@Query("limit") limit: String, @Query("page") page: String, @Query("category") category: String): Call<JsonObject>

    @GET("mobile/subscription")
    fun getMembershipData(): Call<JsonObject>

    @POST("mobile/subscription/purchasePlan")
    fun purchasePlan(@Body mJsonObject: JsonObject): Call<JsonObject>
}//
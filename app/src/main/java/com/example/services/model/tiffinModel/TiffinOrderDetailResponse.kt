package com.example.services.model.tiffinModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class TiffinOrderDetailResponse {

    @SerializedName("code")
    @Expose
    var code: Int? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("body")
    @Expose
    var body: Body? = null

    class Body {
        @SerializedName("orderNo")
        @Expose
        var orderNo: String? = null

        @SerializedName("id")
        @Expose
        var id: String? = null

        @SerializedName("quantity")
        @Expose
        var quantity: String? = null

        @SerializedName("fromDate")
        @Expose
        var fromDate: String? = null

        @SerializedName("endDate")
        @Expose
        var endDate: String? = null

        @SerializedName("tiffinId")
        @Expose
        var tiffinId: String? = null

        @SerializedName("package")
        @Expose
        var _package: String? = null

        @SerializedName("orderPrice")
        @Expose
        var orderPrice: String? = null

        @SerializedName("promoCode")
        @Expose
        var promoCode: Any? = null

        @SerializedName("offerPrice")
        @Expose
        var offerPrice: String? = null

        @SerializedName("serviceCharges")
        @Expose
        var serviceCharges: String? = null

        @SerializedName("excDays")
        @Expose
        var excDays: String? = null

        @SerializedName("excAvailability")
        @Expose
        var excAvailability: String? = null

        @SerializedName("excPrice")
        @Expose
        var excPrice: String? = null

        @SerializedName("totalOrderPrice")
        @Expose
        var totalOrderPrice: String? = null

        @SerializedName("addressId")
        @Expose
        var addressId: String? = null

        @SerializedName("companyId")
        @Expose
        var companyId: String? = null

        @SerializedName("userId")
        @Expose
        var userId: String? = null

        @SerializedName("progressStatus")
        @Expose
        var progressStatus: Int? = null

        @SerializedName("cancellationReason")
        @Expose
        var cancellationReason: String? = null

        @SerializedName("deliveryInstructions")
        @Expose
        var deliveryInstructions: List<String>? = null

        @SerializedName("cookingInstructions")
        @Expose
        var cookingInstructions: String? = null

        @SerializedName("tip")
        @Expose
        var tip: String? = null

        @SerializedName("createdAt")
        @Expose
        var createdAt: String? = null

        @SerializedName("updatedAt")
        @Expose
        var updatedAt: String? = null

        @SerializedName("address")
        @Expose
        var address: Address? = null

        @SerializedName("orderStatus")
        @Expose
        var orderStatus: Any? = null

        @SerializedName("company")
        @Expose
        var company: Company? = null

        @SerializedName("tiffinAssignedEmps")
        @Expose
        var tiffinAssignedEmps: List<TiffinAssignedEmp>? = null
    }

    class Address {
        @SerializedName("id")
        @Expose
        var id: String? = null

        @SerializedName("addressName")
        @Expose
        var addressName: String? = null

        @SerializedName("addressType")
        @Expose
        var addressType: String? = null

        @SerializedName("houseNo")
        @Expose
        var houseNo: String? = null

        @SerializedName("latitude")
        @Expose
        var latitude: String? = null

        @SerializedName("longitude")
        @Expose
        var longitude: String? = null

        @SerializedName("town")
        @Expose
        var town: String? = null

        @SerializedName("landmark")
        @Expose
        var landmark: String? = null

        @SerializedName("city")
        @Expose
        var city: String? = null
    }

    class Company {
        @SerializedName("latitude")
        @Expose
        var latitude: Double? = null

        @SerializedName("longitude")
        @Expose
        var longitude: Double? = null
    }

    class Employee {
        @SerializedName("image")
        @Expose
        var image: String? = null

        @SerializedName("id")
        @Expose
        var id: String? = null

        @SerializedName("firstName")
        @Expose
        var firstName: String? = null

        @SerializedName("lastName")
        @Expose
        var lastName: String? = null

        @SerializedName("countryCode")
        @Expose
        var countryCode: String? = null

        @SerializedName("phoneNumber")
        @Expose
        var phoneNumber: String? = null
    }

    class TiffinAssignedEmp {
        @SerializedName("id")
        @Expose
        var id: String? = null

        @SerializedName("jobStatus")
        @Expose
        var jobStatus: Int? = null

        @SerializedName("employee")
        @Expose
        var employee: Employee? = null
    }

}
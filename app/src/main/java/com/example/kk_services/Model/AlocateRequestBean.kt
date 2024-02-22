package com.example.kk_services.Model


import com.google.gson.annotations.SerializedName

data class AlocateRequestBean(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("error")
    val error: Boolean, // false
    @SerializedName("msg")
    val msg: String // Data loaded successfully.
) {
    data class Data(
        @SerializedName("address")
        val address: String, // ARYA CHOWK, AMBALA HARYANA 134003
        @SerializedName("co_id")
        val coId: Int, // 2
        @SerializedName("comments")
        val comments: Any, // null
        @SerializedName("complain_description")
        val complainDescription: String, // SFA PUMP NOT WORKING 
        @SerializedName("complain_id")
        val complainId: Int, // 970
        @SerializedName("created_date")
        val createdDate: String, // 2024-02-09 15:09:00
        @SerializedName("customer_name")
        val customerName: String, // DR TILAK RAJ GULATI (AMBALA)
        @SerializedName("id")
        val id: Int, // 1099
        @SerializedName("installation_date")
        val installationDate: String, // 2024-01-05 12:30:00
        @SerializedName("mobile")
        val mobile: String, // 9417005548
        @SerializedName("products")
        val products: String, // SANIPRO XR SILENCE
        @SerializedName("secondary_mobile")
        val secondaryMobile: String,
        @SerializedName("service_type")
        val serviceType: String, // service
        @SerializedName("sm_id")
        val smId: Int, // 20
        @SerializedName("status")
        val status: String, // pending
        @SerializedName("updated_date")
        val updatedDate: Any, // null
        @SerializedName("suggestion")
    val suggestion: String,
        @SerializedName("recommendation")
        val recommendation: String, // please provide propershed
    )
}
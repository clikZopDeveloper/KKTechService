package com.example.kk_services.Model


import com.google.gson.annotations.SerializedName

data class DashboardBean(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("error")
    val error: Boolean, // false
    @SerializedName("msg")
    val msg: String // Dataloaded successfully.
) {
    data class Data(
        @SerializedName("allocated_request")
        val allocatedRequest: AllocatedRequest,
        @SerializedName("complaints")
        val complaints: Complaints
    ) {
        data class AllocatedRequest(
            @SerializedName("accepted")
            val accepted: String, // 24
            @SerializedName("pending")
            val pending: String, // 0
            @SerializedName("rejected")
            val rejected: String // 3
        )

        data class Complaints(
            @SerializedName("completed")
            val completed: String, // 0
            @SerializedName("pending")
            val pending: String, // 0
            @SerializedName("processing")
            val processing: String, // 0
            @SerializedName("rejected")
            val rejected: String // 0
        )
    }
}
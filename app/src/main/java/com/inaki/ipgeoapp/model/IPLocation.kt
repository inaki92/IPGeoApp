package com.inaki.ipgeoapp.model


import com.google.gson.annotations.SerializedName

data class IPLocation(
    @SerializedName("as")
    val asX: String? = null,
    @SerializedName("city")
    val city: String? = null,
    @SerializedName("country")
    val country: String? = null,
    @SerializedName("countryCode")
    val countryCode: String? = null,
    @SerializedName("isp")
    val isp: String? = null,
    @SerializedName("lat")
    val lat: Double? = null,
    @SerializedName("lon")
    val lon: Double? = null,
    @SerializedName("org")
    val org: String? = null,
    @SerializedName("query")
    val query: String? = null,
    @SerializedName("region")
    val region: String? = null,
    @SerializedName("regionName")
    val regionName: String? = null,
    @SerializedName("status")
    val status: String? = null,
    @SerializedName("timezone")
    val timezone: String? = null,
    @SerializedName("zip")
    val zip: String? = null
)
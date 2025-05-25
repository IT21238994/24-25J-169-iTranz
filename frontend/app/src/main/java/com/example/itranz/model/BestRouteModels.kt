package com.example.itranz.model

import com.google.gson.annotations.SerializedName

// This matches the overall JSON structure
data class BestRouteResponse(
    val alpha: Double,
    val routes: List<BusRoute>
)

// Matches each object inside "routes" array
data class BusRoute(
    @SerializedName("Date") val date: String,
    @SerializedName("Time") val time: String,
    @SerializedName("Route") val route: String,
    @SerializedName("Starting_Point") val startingPoint: String,
    @SerializedName("Ending_Point") val endingPoint: String,
    @SerializedName("Traffic_Level") val trafficLevel: String,
    @SerializedName("Delay (min)") val delayMin: Int,
    @SerializedName("Distance (km)") val distanceKm: Double,
    @SerializedName("Full Trip Time(min)") val fullTripTimeMin: Int,
    @SerializedName("Seat_Availability (%)") val seatAvailabilityPct: Int,
    @SerializedName("Historical_Peak_Delay (min)") val historicalPeakDelayMin: Int,
    @SerializedName("Status") val status: String,
    @SerializedName("Fare (LKR)") val fareLkr: Int,
    @SerializedName("Average Speed (kmph)") val averageSpeedKmph: Double,
    @SerializedName("Average Bus Stop Time(min)") val averageBusStopTimeMin: Double
)

data class BestRouteRequest(
    @SerializedName("alpha") val alpha: Double,
    @SerializedName("routes") val routes: List<BusRouteInput>
)

/**
 * Each route in the POST request. Must match the exact JSON keys.
 */
data class BusRouteInput(
    @SerializedName("Date") val date: String,
    @SerializedName("Time") val time: String,
    @SerializedName("Route") val route: String,
    @SerializedName("Starting_Point") val startingPoint: String,
    @SerializedName("Ending_Point") val endingPoint: String,
    @SerializedName("Traffic_Level") val trafficLevel: String,
    @SerializedName("Delay (min)") val delayMin: Int,
    @SerializedName("Distance (km)") val distanceKm: Double,
    @SerializedName("Full Trip Time(min)") val fullTripTimeMin: Int,
    @SerializedName("Seat_Availability (%)") val seatAvailabilityPct: Int,
    @SerializedName("Historical_Peak_Delay (min)") val historicalPeakDelayMin: Int,
    @SerializedName("Status") val status: String,
    @SerializedName("Fare (LKR)") val fareLkr: Int,
    @SerializedName("Average Speed (kmph)") val averageSpeedKmph: Double,
    @SerializedName("Average Bus Stop Time(min)") val averageBusStopTimeMin: Double
)

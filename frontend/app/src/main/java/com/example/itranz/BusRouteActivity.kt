package com.example.itranz

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.itranz.model.BestRouteRequest
import com.example.itranz.model.BestRouteResponse
import com.example.itranz.model.BusRouteInput
import com.example.itranz.network.BestRouteRetrofit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BusRouteActivity : AppCompatActivity() {

    private lateinit var etStartingPoint: EditText
    private lateinit var etEndingPoint: EditText
    private lateinit var btnGo: Button
    private lateinit var tvResults: TextView

    // List of routes we want to handle
    private val routeIds = listOf("138", "125", "122")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bus_route)

        etStartingPoint = findViewById(R.id.etStartingPoint)
        etEndingPoint = findViewById(R.id.etEndingPoint)
        btnGo = findViewById(R.id.btnGo)
        tvResults = findViewById(R.id.tvResults)

        // When user clicks "Go"
        btnGo.setOnClickListener {
            // Launch a coroutine so network calls don't block the main thread
            lifecycleScope.launch {
                // 1) Get user input
                val startText = etStartingPoint.text.toString().trim()
                val endText = etEndingPoint.text.toString().trim()

                // 2) Validate or swap if needed
                val (start, end) = validatePoints(startText, endText)

                // 3) Load bus schedules from assets JSON
                val scheduleJson = loadJSONFromAssets("bus_schedules.json")

                // 4) Check if today is weekend
                val isWeekend = isWeekend()

                // 5) Current date/time
                val currentTime = Calendar.getInstance()

                // We'll record the "next three times" for each route, so we can build
                // the final request body afterward.
                val routeNextThreeMap = mutableMapOf<String, List<String>>()

                // 6) Build a results string to show local-schedule info first
                val resultBuilder = StringBuilder()
                resultBuilder.append("Starting: $start\nEnding: $end\n\n")

                // Keep track of earliest next bus time to decide best route
                val routeEarliestTimes = mutableMapOf<String, String?>()

                // 7) For each route, find next 3 available times
                for (routeId in routeIds) {
                    val times = extractTimesForRoute(scheduleJson, routeId, isWeekend, currentTime)

                    if (times.isNotEmpty()) {
                        // Next 3 times only
                        val nextThree = times.take(3)
                        routeNextThreeMap[routeId] = nextThree

                        resultBuilder.append("Route $routeId - Next 3 Buses:\n")
                        nextThree.forEach { time ->
                            resultBuilder.append(" • $time\n")
                        }

                        // The earliest of the route is times[0]
                        routeEarliestTimes[routeId] = times[0]
                    } else {
                        // No upcoming times
                        resultBuilder.append("Route $routeId: No more buses today.\n")
                        routeEarliestTimes[routeId] = null
                    }
                    resultBuilder.append("\n")
                }

                // 8) Determine "best route" from earliest next bus
                val bestRouteId = computeBestRoute(routeEarliestTimes)
                if (bestRouteId != null) {
                    resultBuilder.append("Best Route: $bestRouteId\n\n")
                } else {
                    resultBuilder.append("Best Route: None available.\n\n")
                }

                // 9) Now we build the POST request body using the next 3 times from *all* routes
                // Hardcode other fields for demonstration
                val busRouteInputs = mutableListOf<BusRouteInput>()
                routeNextThreeMap.forEach { (rId, timesList) ->
                    timesList.forEach { timeStr ->
                        // For simplicity, let's pick a dummy date, or you can parse from somewhere
                        val date = "01.09.2024"

                        // Hardcode the rest
                        val input = BusRouteInput(
                            date = date,
                            time = timeStr,
                            route = rId,
                            startingPoint = start,
                            endingPoint = end,
                            trafficLevel = "Low",                // Hardcoded
                            delayMin = 10,                       // Hardcoded
                            distanceKm = 20.5,                   // Hardcoded
                            fullTripTimeMin = 90,                // Hardcoded
                            seatAvailabilityPct = 60,            // Hardcoded
                            historicalPeakDelayMin = 15,         // Hardcoded
                            status = "On Time",                  // Hardcoded
                            fareLkr = 76,                        // Hardcoded
                            averageSpeedKmph = 13.7,             // Hardcoded
                            averageBusStopTimeMin = 2.2          // Hardcoded
                        )
                        busRouteInputs.add(input)
                    }
                }

                val requestBody = BestRouteRequest(
                    alpha = 0.5,         // Hardcode or compute
                    routes = busRouteInputs
                )

                // 10) Perform the POST request to get final best-route data from the server
                try {
                    val response: BestRouteResponse = withContext(Dispatchers.IO) {
                        BestRouteRetrofit.api.getBestRoute(requestBody)
                    }

                    // 11) Append the API response to our result output
                    resultBuilder.append("=== API Response ===\n")
                    resultBuilder.append("Alpha: ${response.alpha}\n\nRoutes Returned:\n")
                    response.routes.forEach { route ->
                        resultBuilder.append(" • ${route.route} - ${route.time}, Delay: ${route.delayMin} min\n")
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    resultBuilder.append("\nError calling API: ${ex.localizedMessage}\n")
                }

                // 12) Show final combined result on screen
                tvResults.text = resultBuilder.toString()
            }
        }
    }

    /**
     * We do a naive approach:
     * - If user typed something other than Kottawa or Colombo Fort, we default to Kottawa or CF.
     * - If reversed, we swap to always be Kottawa -> Colombo Fort (you can modify as needed).
     */
    private fun validatePoints(start: String, end: String): Pair<String, String> {
        var validatedStart = start
        var validatedEnd = end

        // If not recognized, default
        if (validatedStart.lowercase() != "kottawa" && validatedStart.lowercase() != "colombo fort") {
            validatedStart = "Kottawa"
        }
        if (validatedEnd.lowercase() != "kottawa" && validatedEnd.lowercase() != "colombo fort") {
            validatedEnd = "Colombo Fort"
        }

        // If user reversed them (Colombo -> Kottawa), let's fix to Kottawa -> Colombo
        if (validatedStart.equals("colombo fort", ignoreCase = true) &&
            validatedEnd.equals("kottawa", ignoreCase = true)) {
            validatedStart = "Kottawa"
            validatedEnd = "Colombo Fort"
        }
        return Pair(validatedStart, validatedEnd)
    }

    /**
     * Reads JSON from the assets folder (bus_schedules.json).
     */
    private fun loadJSONFromAssets(fileName: String): JSONObject? {
        return try {
            val inputStream: InputStream = assets.open(fileName)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            val jsonString = String(buffer, Charsets.UTF_8)
            JSONObject(jsonString)
        } catch (ex: Exception) {
            ex.printStackTrace()
            null
        }
    }

    /**
     * Checks if today is Saturday or Sunday.
     */
    private fun isWeekend(): Boolean {
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        // Sunday=1, Monday=2, ... Saturday=7
        return (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY)
    }

    /**
     * Extract the upcoming times (after "now") for the specified route.
     * This logic depends on how your JSON is structured.
     */
    private fun extractTimesForRoute(
        scheduleJson: JSONObject?,
        routeId: String,
        isWeekend: Boolean,
        currentTime: Calendar
    ): List<String> {
        if (scheduleJson == null) return emptyList()
        val routeObject = scheduleJson.optJSONObject(routeId) ?: return emptyList()
        val times = mutableListOf<String>()

        val routeType = routeObject.optString("type", "")

        // For route "138", we have weekdaySchedule / weekendSchedule
        if (routeId == "138" && routeType == "both") {
            val timeArray = if (isWeekend) routeObject.optJSONArray("weekendSchedule")
            else routeObject.optJSONArray("weekdaySchedule")
            if (timeArray != null) {
                for (i in 0 until timeArray.length()) {
                    times.add(timeArray.getString(i))
                }
            }
        }
        // For "125" and "122" with 'splitWeekend'
        else if (routeType == "splitWeekend") {
            if (isWeekend) {
                // Combine morning + evening weekend times
                val morningArray = routeObject.optJSONArray("weekendScheduleMorning")
                val eveningArray = routeObject.optJSONArray("weekendScheduleEvening")
                if (morningArray != null) {
                    for (i in 0 until morningArray.length()) {
                        times.add(morningArray.getString(i))
                    }
                }
                if (eveningArray != null) {
                    for (i in 0 until eveningArray.length()) {
                        times.add(eveningArray.getString(i))
                    }
                }
            } else {
                // For weekdays, route "125" has 'weekdaySchedule'
                // route "122" has 'weekdayScheduleMorning' & 'weekdayScheduleEvening'
                if (routeObject.has("weekdaySchedule")) {
                    // route 125
                    val arr = routeObject.optJSONArray("weekdaySchedule")
                    if (arr != null) {
                        for (i in 0 until arr.length()) {
                            times.add(arr.getString(i))
                        }
                    }
                } else {
                    // route 122
                    val morningArray = routeObject.optJSONArray("weekdayScheduleMorning")
                    val eveningArray = routeObject.optJSONArray("weekdayScheduleEvening")
                    if (morningArray != null) {
                        for (i in 0 until morningArray.length()) {
                            times.add(morningArray.getString(i))
                        }
                    }
                    if (eveningArray != null) {
                        for (i in 0 until eveningArray.length()) {
                            times.add(eveningArray.getString(i))
                        }
                    }
                }
            }
        }

        // Filter only future times (after "now"), and sort ascending
        val upcomingTimes = times.filter { isTimeAfterCurrent(it, currentTime) }
        return upcomingTimes.sortedBy { parseTimeToCalendar(it).timeInMillis }
    }

    /**
     * Compare the string time (e.g. "07:00 AM") to currentTime to see if it's still upcoming.
     */
    private fun isTimeAfterCurrent(timeStr: String, currentTime: Calendar): Boolean {
        val calTime = parseTimeToCalendar(timeStr)
        // Set the same day as 'currentTime'
        calTime.set(Calendar.YEAR, currentTime.get(Calendar.YEAR))
        calTime.set(Calendar.MONTH, currentTime.get(Calendar.MONTH))
        calTime.set(Calendar.DAY_OF_MONTH, currentTime.get(Calendar.DAY_OF_MONTH))
        return calTime.timeInMillis >= currentTime.timeInMillis
    }

    /**
     * Convert "07:00 AM" to a Calendar (just hours/minutes).
     */
    private fun parseTimeToCalendar(timeStr: String): Calendar {
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val date = timeFormat.parse(timeStr) ?: return Calendar.getInstance()
        val cal = Calendar.getInstance()
        cal.time = date
        return cal
    }

    /**
     * Simple "best route" logic:
     *    - For each route, look at the earliest upcoming time (if any).
     *    - Pick the route with the earliest time among them.
     */
    private fun computeBestRoute(routeEarliestTimes: Map<String, String?>): String? {
        var bestRoute: String? = null
        var earliestMillis: Long? = null

        for ((routeId, timeStr) in routeEarliestTimes) {
            if (timeStr != null) {
                val calTime = parseTimeToCalendar(timeStr)
                // Align with today's date
                val now = Calendar.getInstance()
                calTime.set(Calendar.YEAR, now.get(Calendar.YEAR))
                calTime.set(Calendar.MONTH, now.get(Calendar.MONTH))
                calTime.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH))

                val routeTimeMillis = calTime.timeInMillis
                if (earliestMillis == null || routeTimeMillis < earliestMillis) {
                    earliestMillis = routeTimeMillis
                    bestRoute = routeId
                }
            }
        }
        return bestRoute
    }
}
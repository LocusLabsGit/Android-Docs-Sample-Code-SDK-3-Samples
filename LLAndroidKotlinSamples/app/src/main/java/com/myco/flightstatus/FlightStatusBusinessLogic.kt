package com.myco.flightstatus

import android.content.Context
import com.locuslabs.sdk.llpublic.LLPOI
import com.myco.R
import java.util.*

fun isArrivalGate(context: Context, poi: LLPOI, flight: Flight): Boolean {
    return (context.getString(R.string.fs_common_gate) + " " + flight.arrivalGate.gate).equals(
        poi.name,
        ignoreCase = true
    )
}

fun isDepartureGate(context: Context, poi: LLPOI, flight: Flight): Boolean {
    return (context.getString(R.string.fs_common_gate) + " " + flight.departureGate.gate).equals(
        poi.name,
        ignoreCase = true
    )
}

/**
 * Create a test departing fly for LAX airport
 */
fun laxConnectingFlight(): Flight {
    val oceanic = Airline.Oceanic()
    val oceanicFlightCode = FlightCode(oceanic, "815")
    val arrivalGate = Flight.AirportGate()
    arrivalGate.airportCode = "lax"
    arrivalGate.airportName = "Los Angeles"
    arrivalGate.gate = "60"
    arrivalGate.baggageClaim = "2"
    val departingGate = Flight.AirportGate()
    departingGate.airportCode = "lax"
    departingGate.airportName = "Los Angeles"
    departingGate.gate = "61"
    departingGate.baggageClaim = "2"
    val arrivalDate = Date(1541440055748L)
    val departureDate = Date(arrivalDate.time + 1000000)
    val arrivalTimes: Flight.Times = Flight.Times()
    arrivalTimes.actual = arrivalDate
    arrivalTimes.estimated = arrivalDate
    arrivalTimes.scheduled = arrivalDate
    val departureTimes: Flight.Times = Flight.Times()
    departureTimes.actual = departureDate
    departureTimes.estimated = departureDate
    departureTimes.scheduled = departureDate
    val oceanicFlight815 = Flight()
    oceanicFlight815.operatingFlightCode = oceanicFlightCode
    oceanicFlight815.departureGate = departingGate
    oceanicFlight815.arrivalGate = arrivalGate
    oceanicFlight815.departureTimes = departureTimes
    oceanicFlight815.arrivalTimes = arrivalTimes
    return oceanicFlight815
}

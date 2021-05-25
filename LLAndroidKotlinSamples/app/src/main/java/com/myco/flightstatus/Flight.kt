package com.myco.flightstatus

import java.util.*

/**
 * Describes a flight
 */
class Flight {
    var operatingFlightCode: FlightCode? = null
    var arrivalGate: AirportGate? = null
    var departureGate: AirportGate? = null
    var arrivalTimes: Times? = null
    var departureTimes: Times? = null

    class AirportGate {
        var airportCode: String? = null
        var airportName: String? = null
        var gate: String? = null
        var baggageClaim: String? = null
    }

    class Times {
        var estimated: Date? = null
        var actual: Date? = null
        var scheduled: Date? = null
    }
}
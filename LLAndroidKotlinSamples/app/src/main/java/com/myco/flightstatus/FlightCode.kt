package com.myco.flightstatus

/**
 * A flight code model
 */
data class FlightCode(
    val airline: Airline,
    val number: String
)
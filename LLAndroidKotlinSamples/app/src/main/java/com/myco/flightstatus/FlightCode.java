package com.myco.flightstatus;

/**
 * A flight code model
 */
public class FlightCode {
    final private Airline airline;
    final private String number;

    public FlightCode(Airline airline, String number) {
        this.airline = airline;
        this.number = number;
    }

    /**
     * @return the airline
     */
    public Airline getAirline() {
        return airline;
    }

    /**
     * @return the flight number
     */
    public String getNumber() {
        return number;
    }
}

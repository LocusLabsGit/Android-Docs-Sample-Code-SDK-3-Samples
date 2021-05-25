package com.myco.flightstatus;

import androidx.annotation.Nullable;

import java.util.Date;

/**
 * Describes a flight
 */
public class Flight {
    private FlightCode operatingFlightCode;
    private AirportGate arrivalGate;
    private AirportGate departureGate;
    private Times arrivalTimes;
    private Times departureTimes;

    public Flight() {
    }

    /**
     * @return flight code for operating airline
     */
    public FlightCode getOperatingFlightCode() {
        return operatingFlightCode;
    }

    /**
     * @param operatingFlightCode flight code for operating airline
     */
    public void setOperatingFlightCode(FlightCode operatingFlightCode) {
        this.operatingFlightCode = operatingFlightCode;
    }

    /**
     * @return arrival gate
     */
    public AirportGate getArrivalGate() {
        return arrivalGate;
    }

    /**
     * @param arrivalGate the arrival gate
     */
    public void setArrivalGate(AirportGate arrivalGate) {
        this.arrivalGate = arrivalGate;
    }

    /**
     * @return the departure gate
     */
    public AirportGate getDepartureGate() {
        return departureGate;
    }

    /**
     * @param departureGate the departure gate
     */
    public void setDepartureGate(AirportGate departureGate) {
        this.departureGate = departureGate;
    }

    /**
     * @return arrival times
     */
    @Nullable
    public Times getArrivalTimes() {
        return arrivalTimes;
    }

    /**
     * @param arrivalTimes arrival times
     */
    public void setArrivalTimes(@Nullable Times arrivalTimes) {
        this.arrivalTimes = arrivalTimes;
    }

    /**
     * @return departure times
     */
    @Nullable
    public Times getDepartureTimes() {
        return departureTimes;
    }

    /**
     * @param departureTimes departure times
     */
    public void setDepartureTimes(@Nullable Times departureTimes) {
        this.departureTimes = departureTimes;
    }

    public static class AirportGate {
        private String airportCode;
        private String airportName;
        private String gate;
        private String baggageClaim;

        /**
         * @return airport code
         */
        public String getAirportCode() {
            return airportCode;
        }

        /**
         * @param airportCode
         */
        public void setAirportCode(String airportCode) {
            this.airportCode = airportCode;
        }

        /**
         * @return airport name
         */
        public String getAirportName() {
            return airportName;
        }

        /**
         * @param airportName
         */
        public void setAirportName(String airportName) {
            this.airportName = airportName;
        }

        /**
         * @return gate
         */
        public String getGate() {
            return gate;
        }

        /**
         * @param gate
         */
        public void setGate(String gate) {
            this.gate = gate;
        }


        /**
         * @return baggageClaim
         */
        public String getBaggageClaim() {
            return baggageClaim;
        }

        /**
         * @param baggageClaim
         */
        public void setBaggageClaim(String baggageClaim) {
            this.baggageClaim = baggageClaim;
        }
    }

    public static class Times {
        private Date estimated;
        private Date actual;
        private Date scheduled;

        /**
         * @return estimated time
         */
        @Nullable
        public Date getEstimated() {
            return estimated;
        }

        /**
         * @param estimated time
         */
        public void setEstimated(@Nullable Date estimated) {
            this.estimated = estimated;
        }

        /**
         * @return actual time
         */
        @Nullable
        public Date getActual() {
            return actual;
        }

        /**
         * @param actual time
         */
        public void setActual(@Nullable Date actual) {
            this.actual = actual;
        }

        /**
         * @return scheduled time
         */
        @Nullable
        public Date getScheduled() {
            return scheduled;
        }

        /**
         * @param scheduled time
         */
        public void setScheduled(@Nullable Date scheduled) {
            this.scheduled = scheduled;
        }
    }
}
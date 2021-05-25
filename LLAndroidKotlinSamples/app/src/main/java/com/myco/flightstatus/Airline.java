package com.myco.flightstatus;

/**
 * Describes an airline
 */
public class Airline {
    static private Airline oceanic = null;
    final private String name;
    final private String smallIconUrl;

    public Airline(String name, String smallIconUrl) {
        this.name = name;
        this.smallIconUrl = smallIconUrl;
    }

    static public Airline Oceanic() {
        if (oceanic == null) {
            oceanic = new Airline("Oceanic Airlines", "oceanic_airlines_logo.png");
        }
        return oceanic;
    }

    /**
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @return small icon url
     */
    public String getSmallIconUrl() {
        return smallIconUrl;
    }
}

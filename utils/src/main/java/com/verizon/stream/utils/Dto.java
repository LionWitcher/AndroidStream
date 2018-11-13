package com.verizon.stream.utils;

import java.io.Serializable;

/**
 * Data Transfer Object
 */
public class Dto implements Serializable {
    private String mEvents;
    private String mGeoData;

    public Dto(String events, String geoData) {
        mEvents = events;
        mGeoData = geoData;
    }

    public String getEvents() {
        return mEvents;
    }

    public String getGeoData() {
        return mGeoData;
    }

    @Override
    public String toString() {
        return String.format("Dto {events=%s, geoData=%s}", mEvents, mGeoData);
    }
}

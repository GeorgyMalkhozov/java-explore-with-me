package ru.practicum.location.dto;

import org.springframework.validation.annotation.Validated;

import javax.persistence.*;

@Validated
public class LocationDto {

    private Float lat;
    private Float lon;

    public Float getLat() {
        return lat;
    }

    public void setLat(Float lat) {
        this.lat = lat;
    }

    public Float getLon() {
        return lon;
    }

    public void setLon(Float lon) {
        this.lon = lon;
    }
}

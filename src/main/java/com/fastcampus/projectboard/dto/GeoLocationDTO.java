package com.fastcampus.projectboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GeoLocationDTO {
    private String ipAddress;
    private String country;
    private String subdivision;
    private String city;
    private double latitude;
    private double longitude;
    private Integer accuracyRadius;
}

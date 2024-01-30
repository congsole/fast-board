package com.fastcampus.projectboard.dto;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GeoLocationDTO {
    private String ipAddress;
    private String country;
    private String subdivision;
}

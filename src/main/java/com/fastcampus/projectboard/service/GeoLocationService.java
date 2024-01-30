package com.fastcampus.projectboard.service;

import com.fastcampus.projectboard.dto.GeoLocationDTO;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.Country;
import com.maxmind.geoip2.record.Subdivision;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.InetAddress;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeoLocationService {
    private final DatabaseReader databaseReader;

    public GeoLocationDTO getGeoLocation(String ipAddress) {
        if(ipAddress.isEmpty() || ipAddress.isBlank()) {
            log.warn("ip 주소가 없어서 위치 정보를 얻지 못함");
            return null;
        }
        try {
            CityResponse cityResponse = databaseReader.city(InetAddress.getByName(ipAddress));
            Country country = cityResponse.getCountry();
            Subdivision subdivision = cityResponse.getMostSpecificSubdivision();
            return new GeoLocationDTO(ipAddress, country.getName(), subdivision.getName());
        } catch(Exception e) {
            return null;
        }
    }
}

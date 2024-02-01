package com.fastcampus.projectboard.service;

import com.fastcampus.projectboard.dto.GeoLocationDTO;
import com.fastcampus.projectboard.dto.NaverGeoLocationDto;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.AsnResponse;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Country;
import com.maxmind.geoip2.record.Location;
import com.maxmind.geoip2.record.Subdivision;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.hibernate.type.descriptor.java.spi.JsonJavaType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URI;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeoLocationService {
    private final DatabaseReader databaseReader;
    private final RestTemplate restTemplate;
    private final String hostName = "https://geolocation.apigw.ntruss.com";
    private final String requestUrl= "/geolocation/v2/geoLocation";

    @Value("${naver.rest.api.access}")
    private String access;
    @Value("${naver.rest.api.secret}")
    private String secret;


    public GeoLocationDTO getGeoLocation(String ipAddress) {
        if(ipAddress.isEmpty() || ipAddress.isBlank()) {
            log.warn("ip 주소가 없어서 위치 정보를 얻지 못함");
            return null;
        }
        try {
            CityResponse cityResponse = databaseReader.city(InetAddress.getByName(ipAddress));
                Country country = cityResponse.getCountry();
                Subdivision subdivision = cityResponse.getMostSpecificSubdivision();
                City city = cityResponse.getCity();
                Location location = cityResponse.getLocation();

            return new GeoLocationDTO(ipAddress, country.getName(), subdivision.getName(), city.getName(), location.getLatitude(), location.getLongitude(), location.getAccuracyRadius());
        } catch(Exception e) {
            return null;
        }
    }



    public String getNaverGeoLocation(String ip) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {



        String baseString = requestUrl + "?" +
                "ip=" + ip +
                "&ext=t" +
                "&responseFormatType=json";
        System.out.println("★★★" + baseString);
        //
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(hostName + baseString);
        URI uri = uriBuilder.build().encode().toUri();


        // signature 생성
        String timestamp = Long.toString(System.currentTimeMillis());
        String signature = makeSignature(baseString, timestamp, access, secret);

        // header 셋팅하기
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-ncp-apigw-timestamp", timestamp);
        headers.set("x-ncp-iam-access-key", access);
        headers.set("x-ncp-apigw-signature-v2", signature);
        HttpEntity httpEntity = new HttpEntity<>(headers);

        // exchange
        String json = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, String.class).getBody();


        // return
        return json;
    }

    private static String makeSignature(String baseString, String timestamp, String accessKey, String secretKey) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        String signature;
        String space = " ";
        String newLine = "\n";

        String rawString = new StringBuilder()
                .append("GET")
                .append(space)
                .append(baseString)
                .append(newLine)
                .append(timestamp)
                .append(newLine)
                .append(accessKey)
                .toString();

        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(secretKeySpec);
        byte[] rawHmac = mac.doFinal(rawString.getBytes("UTF-8"));

        signature = Base64.encodeBase64String(rawHmac);
        return signature;
    }
}

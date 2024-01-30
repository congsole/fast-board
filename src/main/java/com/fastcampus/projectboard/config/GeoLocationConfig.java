package com.fastcampus.projectboard.config;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;

@Configuration
public class GeoLocationConfig {
    @Bean("databaseReader")
    public DatabaseReader databaseReader() throws IOException, GeoIp2Exception {
        File resource = new File("./geoip2/GeoLite2-ASN.mmdb");
        return new DatabaseReader.Builder(resource).build();
    }
}

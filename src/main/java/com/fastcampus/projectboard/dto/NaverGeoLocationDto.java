package com.fastcampus.projectboard.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NaverGeoLocationDto {
    private String requestId; // API 요청 ID
    private String returnCode; // 정상코드 0, 이외는 오류코드
    private String country; // 국가 코드
    private String code; // 행정구역 코드
    private String r1; // 도, 광역시, 주
    private String r2; // 시, 군, 구
    private String r3; // 동, 면 읍
    private float lat; // 위도
    @JsonProperty("long")
    private float lon; // 경도
    private String net; // 통신사 잉름

}

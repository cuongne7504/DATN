package com.example.backend.controller;

import com.example.backend.dto.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ghn")
@CrossOrigin(origins = "*")
public class GhnController {

    @Value("${ghn.api.token}")
    private String token;

    @Value("${ghn.shop.id}")
    private String shopId;

    @Value("${ghn.shop.district-id:3440}")
    private Integer fromDistrictId;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private Object parseJson(String json) {
        try {
            return objectMapper.readValue(json, Object.class);
        } catch (Exception e) {
            return json;
        }
    }

    @GetMapping("/provinces")
    public ApiResponse<Object> getProvinces() {
        String url = "https://dev-online-gateway.ghn.vn/shiip/public-api/master-data/province";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Token", token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        return ApiResponse.ok(parseJson(response.getBody()));
    }

    @GetMapping("/districts")
    public ApiResponse<Object> getDistricts(@RequestParam("provinceId") Integer provinceId) {
        String url = "https://dev-online-gateway.ghn.vn/shiip/public-api/master-data/district?province_id=" + provinceId;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Token", token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        return ApiResponse.ok(parseJson(response.getBody()));
    }

    @GetMapping("/wards")
    public ApiResponse<Object> getWards(@RequestParam("districtId") Integer districtId) {
        String url = "https://dev-online-gateway.ghn.vn/shiip/public-api/master-data/ward?district_id=" + districtId;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Token", token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        return ApiResponse.ok(parseJson(response.getBody()));
    }

    @PostMapping("/fee")
    public ApiResponse<Object> getFee(@RequestBody Map<String, Object> request) {
        String url = "https://dev-online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/fee";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Token", token);
        headers.set("ShopId", shopId);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("from_district_id", fromDistrictId);
        body.put("to_district_id", request.get("to_district_id"));
        body.put("to_ward_code", request.get("to_ward_code"));
        body.put("service_type_id", 2); // Chuẩn đường bộ thông thường
        body.put("insurance_value", 0);
        body.put("coupon", null);
        body.put("height", 15);
        body.put("length", 15);
        body.put("width", 15);
        body.put("weight", 500); // Mặc định 500g

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        return ApiResponse.ok(parseJson(response.getBody()));
    }
}

package com.example.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class GhnService {

    @Value("${ghn.api.token:0057af57-7b8d-11f1-a973-aee5264794df}")
    private String ghnApiToken;

    @Value("${ghn.shop.id:201034}")
    private String ghnShopId;

    @Value("${ghn.shop.district-id:1442}")
    private Integer ghnShopDistrictId;

    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Object> getProvinces() {
        String url = "https://dev-online-gateway.ghn.vn/shiip/public-api/master-data/province";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Token", ghnApiToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        Map<String, Object> resBody = response.getBody();
        if (resBody != null && resBody.containsKey("data") && resBody.get("data") instanceof java.util.List) {
            java.util.List<Map<String, Object>> list = (java.util.List<Map<String, Object>>) resBody.get("data");
            java.util.List<Map<String, Object>> filtered = list.stream()
                .filter(item -> {
                    String name = String.valueOf(item.get("ProvinceName")).toLowerCase();
                    return !name.contains("test") && !name.contains("alert") && !name.contains("đặc biệt") && !name.contains("hà nội 02");
                })
                .collect(java.util.stream.Collectors.toList());
            resBody.put("data", filtered);
        }
        return resBody;
    }

    public Map<String, Object> getDistricts(Integer provinceId) {
        String url = "https://dev-online-gateway.ghn.vn/shiip/public-api/master-data/district?province_id=" + provinceId;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Token", ghnApiToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        Map<String, Object> resBody = response.getBody();
        if (resBody != null && resBody.containsKey("data") && resBody.get("data") instanceof java.util.List) {
            java.util.List<Map<String, Object>> list = (java.util.List<Map<String, Object>>) resBody.get("data");
            java.util.List<Map<String, Object>> filtered = list.stream()
                .filter(item -> {
                    String name = String.valueOf(item.get("DistrictName")).toLowerCase();
                    return !name.contains("test") && !name.contains("alert") && !name.contains("đặc biệt") && !name.contains("hà nội 02");
                })
                .collect(java.util.stream.Collectors.toList());
            resBody.put("data", filtered);
        }
        return resBody;
    }

    public Map<String, Object> getWards(Integer districtId) {
        String url = "https://dev-online-gateway.ghn.vn/shiip/public-api/master-data/ward?district_id=" + districtId;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Token", ghnApiToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        Map<String, Object> resBody = response.getBody();
        if (resBody != null && resBody.containsKey("data") && resBody.get("data") instanceof java.util.List) {
            java.util.List<Map<String, Object>> list = (java.util.List<Map<String, Object>>) resBody.get("data");
            java.util.List<Map<String, Object>> filtered = list.stream()
                .filter(item -> {
                    String name = String.valueOf(item.get("WardName")).toLowerCase();
                    return !name.contains("test") && !name.contains("alert") && !name.contains("đặc biệt");
                })
                .collect(java.util.stream.Collectors.toList());
            resBody.put("data", filtered);
        }
        return resBody;
    }

    public Map<String, Object> calculateFee(Integer toDistrictId, String toWardCode) {
        String url = "https://dev-online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/fee";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Token", ghnApiToken);
        headers.set("ShopId", ghnShopId);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("from_district_id", ghnShopDistrictId); // Shop's district (configured in application.properties)
        body.put("service_type_id", 2); // Standard delivery
        body.put("to_district_id", toDistrictId);
        body.put("to_ward_code", toWardCode);
        body.put("weight", 500);
        body.put("length", 20);
        body.put("width", 15);
        body.put("height", 10);
        body.put("insurance_value", 0);
        body.put("cod_failed_amount", 0);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
        return response.getBody();
    }
}

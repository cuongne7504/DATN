package com.example.backend.controller;

import com.example.backend.dto.ApiResponse;
import com.example.backend.service.GhnService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ghn")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GhnController {

    private final GhnService ghnService;

    @GetMapping("/provinces")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getProvinces() {
        return ResponseEntity.ok(ApiResponse.ok(ghnService.getProvinces()));
    }

    @GetMapping("/districts")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDistricts(@RequestParam Integer provinceId) {
        return ResponseEntity.ok(ApiResponse.ok(ghnService.getDistricts(provinceId)));
    }

    @GetMapping("/wards")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getWards(@RequestParam Integer districtId) {
        return ResponseEntity.ok(ApiResponse.ok(ghnService.getWards(districtId)));
    }

    @PostMapping("/fee")
    public ResponseEntity<ApiResponse<Map<String, Object>>> calculateFee(@RequestBody Map<String, Object> request) {
        Integer toDistrictId = (Integer) request.get("to_district_id");
        String toWardCode = (String) request.get("to_ward_code");
        return ResponseEntity.ok(ApiResponse.ok(ghnService.calculateFee(toDistrictId, toWardCode)));
    }
}

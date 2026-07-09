package com.example.backend.controller;

import com.example.backend.dto.ApiResponse;
import com.example.backend.dto.OtpRequest;
import com.example.backend.service.OtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/otp")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OtpController {

    private final OtpService otpService;

    @PostMapping("/send")
    public ResponseEntity<ApiResponse<String>> sendOtp(@Valid @RequestBody OtpRequest request) {
        String otpCode = otpService.generateAndSendOtp(request.getSoDienThoai());
        return ResponseEntity.ok(ApiResponse.ok("Đã gửi mã OTP đến số điện thoại " + request.getSoDienThoai(), null));
    }
}

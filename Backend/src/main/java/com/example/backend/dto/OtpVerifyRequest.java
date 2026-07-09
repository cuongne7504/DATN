package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OtpVerifyRequest {
    @NotBlank(message = "Số điện thoại không được để trống")
    private String soDienThoai;
    
    @NotBlank(message = "Mã OTP không được để trống")
    private String otpCode;
}

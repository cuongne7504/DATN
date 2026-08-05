package com.example.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {

    @Value("${speedsms.access-token:}")
    private String accessToken;
    @Value("${speedsms.sms-type:4}")
    private Integer smsType;
    @Value("${speedsms.sender:}")
    private String senderName;

    private final RestTemplate restTemplate = new RestTemplate();

    // Lưu OTP kèm thời gian tạo. Key: Số điện thoại, Value: OTP. (Thực tế nên dùng Redis)
    private final ConcurrentHashMap<String, String> otpStorage = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> otpExpiry = new ConcurrentHashMap<>();

    private static final long OTP_VALID_DURATION = 3 * 60 * 1000; // 3 phút

    public String generateAndSendOtp(String toPhoneNumber) {
        // Format lại số điện thoại Việt Nam dạng 84xxxxxxx cho SpeedSMS (nếu bắt đầu bằng 0)
        String formattedPhone = toPhoneNumber.trim();
        if (formattedPhone.startsWith("0")) {
            formattedPhone = "84" + formattedPhone.substring(1);
        }

        // Tạo mã ngẫu nhiên 6 số
        String otpCode = String.format("%06d", new Random().nextInt(999999));
        
        // Lưu vào bộ nhớ tạm
        otpStorage.put(toPhoneNumber, otpCode);
        otpExpiry.put(toPhoneNumber, System.currentTimeMillis() + OTP_VALID_DURATION);

        // Kiểm tra xem đã cấu hình SpeedSMS chưa
        if (accessToken == null || accessToken.trim().isEmpty()) {
            System.out.println("======== MÔ PHỎNG SMS (SPEEDSMS) ========");
            System.out.println("Gửi tới: " + formattedPhone);
            System.out.println("Mã OTP: " + otpCode);
            System.out.println("=========================================");
            return otpCode;
        }

        try {
            // Chuẩn bị URL và body cho SpeedSMS
            String url = "https://api.speedsms.vn/index.php/sms/send";

            // Headers Basic Authentication
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // Basic Auth: AccessToken:x
            String auth = accessToken + ":x";
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            headers.set("Authorization", "Basic " + encodedAuth);

            // Request body
            Map<String, Object> body = new HashMap<>();
            body.put("to", List.of(formattedPhone));
            body.put("content", "Ma xac thuc SportPro cua ban la " + otpCode + ". Ma co hieu luc trong 3 phut.");
            body.put("sms_type", smsType);
            body.put("sender", senderName != null ? senderName : "");

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            System.out.println("Đang gửi SMS qua SpeedSMS tới: " + formattedPhone);
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map responseBody = response.getBody();
                System.out.println("Kết quả SpeedSMS: " + responseBody);
            } else {
                System.err.println("Gửi SMS qua SpeedSMS thất bại, status code: " + response.getStatusCode());
            }

        } catch (Exception e) {
            System.err.println("Lỗi gửi SMS qua SpeedSMS: " + e.getMessage());
            // In ra OTP fallback để test nếu gặp lỗi
            System.out.println("Mã OTP (Fallback): " + otpCode);
        }

        return otpCode;
    }

    public boolean verifyOtp(String phoneNumber, String inputCode) {
        // OTP Mặc định để test dễ dàng nếu không nhận được SMS (Hữu ích khi bảo vệ ĐATN)
        if ("123456".equals(inputCode)) {
            return true;
        }

        String savedCode = otpStorage.get(phoneNumber);
        Long expiryTime = otpExpiry.get(phoneNumber);

        if (savedCode == null || expiryTime == null) {
            return false; // Không có OTP hoặc chưa yêu cầu
        }

        if (System.currentTimeMillis() > expiryTime) {
            otpStorage.remove(phoneNumber);
            otpExpiry.remove(phoneNumber);
            return false; // Hết hạn
        }

        if (savedCode.equals(inputCode)) {
            // Xác thực thành công thì xóa luôn
            otpStorage.remove(phoneNumber);
            otpExpiry.remove(phoneNumber);
            return true;
        }

        return false;
    }
}

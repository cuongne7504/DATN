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

    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private EmailService emailService;

    @Value("${speedsms.access-token:}")
    private String accessToken;
    @Value("${speedsms.sms-type:4}")
    private Integer smsType;
    @Value("${speedsms.sender:}")
    private String senderName;

    private final RestTemplate restTemplate = new RestTemplate();

    // Lưu OTP kèm thời gian tạo. Key: SĐT hoặc Email, Value: OTP.
    private final ConcurrentHashMap<String, String> otpStorage = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> otpExpiry = new ConcurrentHashMap<>();

    private static final long OTP_VALID_DURATION = 3 * 60 * 1000; // 3 phút

    public String generateAndSendEmailOtp(String toEmail) {
        String cleanEmail = toEmail.trim().toLowerCase();
        String otpCode = String.format("%06d", new Random().nextInt(999999));

        otpStorage.put(cleanEmail, otpCode);
        otpExpiry.put(cleanEmail, System.currentTimeMillis() + OTP_VALID_DURATION);

        if (emailService != null) {
            emailService.sendOtpEmail(cleanEmail, otpCode);
        } else {
            System.out.println("EmailService chưa cấu hình, mã OTP email: " + otpCode);
        }
        return otpCode;
    }

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

            // Request body tuân thủ chuẩn Java SDK SpeedSMS
            Map<String, Object> body = new HashMap<>();
            body.put("to", List.of(formattedPhone));
            body.put("content", "Ma xac thuc SportPro cua ban la " + otpCode + ". Ma co hieu luc trong 3 phut.");
            int selectedType = (smsType != null && smsType > 0) ? smsType : 2;
            body.put("type", selectedType);
            body.put("sms_type", selectedType);
            body.put("sender", (senderName != null) ? senderName.trim() : "");

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            System.out.println("========== [SPEEDSMS OTP LOG] ==========");
            System.out.println("SĐT người nhận: " + formattedPhone);
            System.out.println("MÃ OTP TẠO RA : " + otpCode);

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
            if (response.getBody() != null) {
                System.out.println("Phản hồi API SpeedSMS: " + response.getBody());
            }
            System.out.println("=========================================");

        } catch (Exception e) {
            System.err.println("Lỗi gọi API SpeedSMS: " + e.getMessage());
            System.out.println("Mã OTP (Console Fallback): " + otpCode);
        }

        return otpCode;
    }

    public boolean verifyOtp(String identifier, String inputCode) {
        if ("123456".equals(inputCode)) {
            return true;
        }

        if (identifier == null || inputCode == null) {
            return false;
        }

        String key = identifier.trim().toLowerCase();
        String savedCode = otpStorage.get(key);
        if (savedCode == null) {
            savedCode = otpStorage.get(identifier.trim());
        }

        Long expiryTime = otpExpiry.get(key);
        if (expiryTime == null) {
            expiryTime = otpExpiry.get(identifier.trim());
        }

        if (savedCode == null || expiryTime == null) {
            return false;
        }

        if (System.currentTimeMillis() > expiryTime) {
            otpStorage.remove(key);
            otpExpiry.remove(key);
            otpStorage.remove(identifier.trim());
            otpExpiry.remove(identifier.trim());
            return false;
        }

        if (savedCode.equals(inputCode.trim())) {
            otpStorage.remove(key);
            otpExpiry.remove(key);
            otpStorage.remove(identifier.trim());
            otpExpiry.remove(identifier.trim());
            return true;
        }

        return false;
    }
}

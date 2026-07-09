package com.example.backend.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {

    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.auth-token}")
    private String authToken;

    @Value("${twilio.phone-number}")
    private String fromPhoneNumber;

    // Lưu OTP kèm thời gian tạo. Key: Số điện thoại, Value: OTP. (Thực tế nên dùng Redis)
    private final ConcurrentHashMap<String, String> otpStorage = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> otpExpiry = new ConcurrentHashMap<>();

    private static final long OTP_VALID_DURATION = 3 * 60 * 1000; // 3 phút

    public String generateAndSendOtp(String toPhoneNumber) {
        // Format lại số điện thoại Việt Nam (+84) cho Twilio
        String formattedPhone = toPhoneNumber;
        if (formattedPhone.startsWith("0")) {
            formattedPhone = "+84" + formattedPhone.substring(1);
        }

        // Tạo mã ngẫu nhiên 6 số
        String otpCode = String.format("%06d", new Random().nextInt(999999));
        
        // Lưu vào bộ nhớ tạm
        otpStorage.put(toPhoneNumber, otpCode);
        otpExpiry.put(toPhoneNumber, System.currentTimeMillis() + OTP_VALID_DURATION);

        // Kiểm tra xem đã cấu hình Twilio chưa
        if (accountSid == null || accountSid.trim().isEmpty()) {
            System.out.println("======== MÔ PHỎNG SMS ========");
            System.out.println("Gửi tới: " + formattedPhone);
            System.out.println("Mã OTP: " + otpCode);
            System.out.println("===============================");
            return otpCode;
        }

        try {
            // Gửi SMS qua Twilio
            Twilio.init(accountSid, authToken);
            Message message = Message.creator(
                    new PhoneNumber(formattedPhone),
                    new PhoneNumber(fromPhoneNumber),
                    "Mã xác thực SportPro của bạn là: " + otpCode + ". Mã có hiệu lực trong 3 phút."
            ).create();
            
            System.out.println("Đã gửi SMS qua Twilio: " + message.getSid());
        } catch (Exception e) {
            System.err.println("Lỗi gửi SMS qua Twilio: " + e.getMessage());
            // Fallback: Nếu lỗi (do chưa verify số đt ở Twilio trial), vẫn trả về OTP để test
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

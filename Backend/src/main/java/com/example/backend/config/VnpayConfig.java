package com.example.backend.config;

import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Configuration
public class VnpayConfig {
    
    // TODO: Cường thay 2 biến này bằng thông tin lấy trên web Sandbox nhé!
    public static final String vnp_TmnCode = "YOUR_TMN_CODE"; 
    public static final String vnp_HashSecret = "YOUR_HASH_SECRET";
    
    public static final String vnp_Url = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    public static final String vnp_ReturnUrl = "http://localhost:8080/api/payment/vnpay-return";

    /**
     * Thuật toán mã hóa HMAC SHA512 theo chuẩn của VNPAY
     * (Không được sửa hàm này)
     */
    public static String hmacSHA512(final String key, final String data) {
        try {
            if (key == null || data == null) {
                throw new NullPointerException();
            }
            final javax.crypto.Mac hmac512 = javax.crypto.Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes();
            final javax.crypto.spec.SecretKeySpec secretKey = new javax.crypto.spec.SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception ex) {
            return "";
        }
    }
}

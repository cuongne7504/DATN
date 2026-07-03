package com.example.backend.controller;

import com.example.backend.config.VnpayConfig;
import com.example.backend.dto.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*")
public class PaymentController {

    // 1. API sinh URL cho khách bấm vào để quẹt thẻ
    @GetMapping("/create-url")
    public ApiResponse<String> createPaymentUrl(@RequestParam Integer amout_vnd, @RequestParam Integer order_id) throws UnsupportedEncodingException {
        
        long amount = amout_vnd * 100L; // VNPAY yêu cầu nhân 100
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", "2.1.0");
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", VnpayConfig.vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", String.valueOf(order_id) + "_" + System.currentTimeMillis()); // Mã đơn hàng duy nhất
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang " + order_id);
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", VnpayConfig.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", "127.0.0.1");

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        vnp_Params.put("vnp_CreateDate", formatter.format(cld.getTime()));
        
        cld.add(Calendar.MINUTE, 15);
        vnp_Params.put("vnp_ExpireDate", formatter.format(cld.getTime()));

        // Sắp xếp params theo A-Z
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = VnpayConfig.hmacSHA512(VnpayConfig.vnp_HashSecret, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = VnpayConfig.vnp_Url + "?" + queryUrl;

        return ApiResponse.ok("Tạo URL thanh toán thành công", paymentUrl);
    }

    // 2. Webhook IPN (VNPAY tự động gọi ngầm vào đây sau khi khách trả tiền xong)
    @GetMapping("/vnpay-ipn")
    public String vnpayIpn(@RequestParam Map<String, String> queryParams) {
        // Lấy vnp_ResponseCode
        String responseCode = queryParams.get("vnp_ResponseCode");
        
        if ("00".equals(responseCode)) {
            // Thanh toán thành công -> Viết logic cập nhật LICH_SU_THANH_TOAN vào đây
            System.out.println("Giao dịch thành công. Cập nhật DB...");
            return "{\"RspCode\":\"00\",\"Message\":\"Confirm Success\"}";
        } else {
            // Thanh toán thất bại
            return "{\"RspCode\":\"99\",\"Message\":\"Fail\"}";
        }
    }

    // 3. VNPay Return URL - redirect khách hàng về frontend sau thanh toán
    @GetMapping("/vnpay-return")
    public void vnpayReturn(@RequestParam Map<String, String> queryParams, 
                            jakarta.servlet.http.HttpServletResponse response) throws java.io.IOException {
        String responseCode = queryParams.get("vnp_ResponseCode");
        String frontendUrl = "http://localhost:5173/history";
        
        if ("00".equals(responseCode)) {
            // Thanh toán thành công
            System.out.println("VNPay thanh toán thành công, redirect về frontend...");
            response.sendRedirect(frontendUrl + "?payment=success");
        } else {
            // Thanh toán thất bại
            response.sendRedirect(frontendUrl + "?payment=failed");
        }
    }
}

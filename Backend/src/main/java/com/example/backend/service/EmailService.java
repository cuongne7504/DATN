package com.example.backend.service;

import com.example.backend.entity.DonHang;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

@Service
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender javaMailSender;

    public void sendOrderReceipt(DonHang donHang, String emailNhan) {
        if (javaMailSender == null || emailNhan == null || emailNhan.trim().isEmpty()) {
            System.out.println("Không thể gửi email vì chưa cấu hình JavaMailSender hoặc email người nhận trống.");
            return;
        }

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(emailNhan);
            helper.setSubject("Xác nhận đơn hàng #" + donHang.getMaDonHang() + " từ SportPro");

            // Format tiền tệ
            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            String tongTienStr = formatter.format(donHang.getTongTien() != null ? donHang.getTongTien() : BigDecimal.ZERO);

            // Nội dung HTML cơ bản
            String htmlContent = "<h2>Cảm ơn bạn đã đặt hàng tại SportPro!</h2>"
                    + "<p>Chào bạn,</p>"
                    + "<p>Đơn hàng của bạn đã được ghi nhận trên hệ thống với thông tin như sau:</p>"
                    + "<ul>"
                    + "<li><strong>Mã đơn hàng:</strong> " + donHang.getMaDonHang() + "</li>"
                    + "<li><strong>Địa chỉ giao hàng:</strong> " + donHang.getDiaChiGiao() + "</li>"
                    + "<li><strong>Tổng thanh toán:</strong> <span style='color:red;font-weight:bold'>" + tongTienStr + "</span></li>"
                    + "</ul>"
                    + "<p>Chúng tôi sẽ sớm liên hệ với bạn để giao hàng.</p>"
                    + "<p>Trân trọng,<br>Đội ngũ SportPro</p>";

            helper.setText(htmlContent, true); // true = isHtml

            javaMailSender.send(message);
            System.out.println("Đã gửi email xác nhận thành công tới: " + emailNhan);

        } catch (Exception e) {
            System.err.println("Lỗi gửi email xác nhận đơn hàng: " + e.getMessage());
        }
    }
}

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
            // Đường dẫn tra cứu đơn hàng tự động điền mã (Localhost phục vụ đồ án)
            String lookupUrl = "http://localhost:5174/lookup?code=" + donHang.getMaDonHang();

            // Nội dung HTML thiết kế chuyên nghiệp, nổi bật mã đơn hàng
            String htmlContent = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e2e8f0; border-radius: 12px; background-color: #ffffff;'>"
                    + "  <div style='text-align: center; border-bottom: 2px solid #3b82f6; padding-bottom: 15px; margin-bottom: 20px;'>"
                    + "    <h2 style='color: #1e3a8a; margin: 0; text-transform: uppercase; letter-spacing: 1px;'>SPORTPRO</h2>"
                    + "    <p style='color: #6b7280; font-size: 14px; margin: 5px 0 0 0;'>Cửa hàng thời trang & dụng cụ thể thao chuyên nghiệp</p>"
                    + "  </div>"
                    + "  "
                    + "  <h3 style='color: #10b981; margin-top: 0;'>Cảm ơn bạn đã tin tưởng đặt hàng tại SportPro!</h3>"
                    + "  <p style='color: #374151; line-height: 1.6;'>Chào bạn,</p>"
                    + "  <p style='color: #374151; line-height: 1.6;'>Đơn hàng của bạn đã được ghi nhận thành công trên hệ thống. Dưới đây là thông tin chi tiết đơn hàng của bạn:</p>"
                    + "  "
                    + "  <div style='background-color: #f8fafc; border: 1px solid #e2e8f0; border-radius: 8px; padding: 15px; margin: 20px 0;'>"
                    + "    <div style='font-size: 18px; font-weight: bold; color: #1e3a8a; margin-bottom: 10px; text-align: center;'>"
                    + "      MÃ ĐƠN HÀNG CỦA BẠN: <span style='color: #3b82f6; font-size: 22px;'>#" + donHang.getMaDonHang() + "</span>"
                    + "    </div>"
                    + "    <div style='border-top: 1px dashed #cbd5e1; padding-top: 10px; font-size: 14px; color: #334155;'>"
                    + "      <p style='margin: 5px 0;'><strong>Địa chỉ giao hàng:</strong> " + (donHang.getDiaChiGiao() != null ? donHang.getDiaChiGiao() : "Nhận tại quầy") + "</p>"
                    + "      <p style='margin: 5px 0;'><strong>Phương thức thanh toán:</strong> " + donHang.getPhuongThucTt() + "</p>"
                    + "      <p style='margin: 5px 0; font-size: 16px;'><strong>Tổng thanh toán:</strong> <span style='color: #ef4444; font-weight: bold;'>" + tongTienStr + "</span></p>"
                    + "    </div>"
                    + "  </div>"
                    + "  "
                    + "  <p style='color: #374151; line-height: 1.6; text-align: center;'>Bạn có thể nhấn trực tiếp vào nút dưới đây để tra cứu hành trình vận chuyển, tình trạng xử lý đơn hàng của bạn:</p>"
                    + "  "
                    + "  <div style='text-align: center; margin: 25px 0;'>"
                    + "    <a href='" + lookupUrl + "' target='_blank' style='background-color: #3b82f6; color: #ffffff; text-decoration: none; padding: 12px 30px; font-weight: bold; border-radius: 30px; display: inline-block; box-shadow: 0 4px 6px -1px rgba(59, 130, 246, 0.3);'>TRA CỨU ĐƠN HÀNG NGAY</a>"
                    + "  </div>"
                    + "  "
                    + "  <p style='color: #6b7280; font-size: 13px; line-height: 1.6; border-top: 1px solid #e2e8f0; padding-top: 15px; margin-top: 25px;'>"
                    + "    * Lưu ý: Hãy giữ lại email này và mã đơn hàng của bạn để đối chiếu khi cần thiết. Nếu có bất kỳ câu hỏi nào, vui lòng liên hệ hotline 1900 6868.<br><br>"
                    + "    Trân trọng,<br>"
                    + "    <strong>Đội ngũ SportPro</strong>"
                    + "  </p>"
                    + "</div>";

            helper.setText(htmlContent, true); // true = isHtml

            javaMailSender.send(message);
            System.out.println("Đã gửi email xác nhận thành công tới: " + emailNhan);

        } catch (Exception e) {
            System.err.println("Lỗi gửi email xác nhận đơn hàng: " + e.getMessage());
        }
    }
}

package com.example.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class GuestCheckoutRequest {

    @NotBlank(message = "Họ tên không được để trống")
    private String hoTen;

    @NotBlank(message = "Số điện thoại không được để trống")
    private String soDienThoai;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    @NotBlank(message = "Địa chỉ giao hàng không được để trống")
    private String diaChiGiao;

    @NotBlank(message = "Phương thức thanh toán không được để trống")
    private String phuongThucTt;

    private Integer maKhuyenMai;

    private BigDecimal phiShip;

    @NotNull(message = "Danh sách sản phẩm không được để trống")
    private List<TaoDonHangRequest.DonHangItemRequest> items;

    // Phục vụ tính năng OTP
    private String otpCode;
}

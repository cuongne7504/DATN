package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class TaoDonHangRequest {

    @NotNull(message = "Mã người dùng không được để trống")
    private Integer maNguoiDung;

    private Integer maKhuyenMai;

    @NotBlank(message = "Địa chỉ giao hàng không được để trống")
    private String diaChiGiao;

    @NotBlank(message = "Phương thức thanh toán không được để trống")
    private String phuongThucTt;

    private BigDecimal phiShip;

    @NotNull(message = "Danh sách sản phẩm không được để trống")
    private List<DonHangItemRequest> items;

    @Getter
    @Setter
    public static class DonHangItemRequest {
        @NotNull
        private Integer maChiTietSp;

        @NotNull
        private Integer soLuong;

        @NotNull
        private BigDecimal donGia;
    }
}

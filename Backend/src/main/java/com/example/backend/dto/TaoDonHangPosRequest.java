package com.example.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class TaoDonHangPosRequest {

    @NotNull(message = "Mã nhân viên không được để trống")
    private Integer maNhanVien;

    private String tenNguoiNhan;
    
    private String soDienThoai;

    @Valid
    @NotNull(message = "Danh sách sản phẩm không được để trống")
    private List<DonHangItemRequest> items;

    private Integer maKhuyenMai;

    @Data
    public static class DonHangItemRequest {
        @NotNull(message = "Mã biến thể sản phẩm không được để trống")
        private Integer maChiTietSp;

        @NotNull(message = "Số lượng không được để trống")
        private Integer soLuong;

        @NotNull(message = "Đơn giá không được để trống")
        private BigDecimal donGia;
    }
}

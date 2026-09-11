package com.example.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SanPhamRequest {

    @NotNull(message = "Mã danh mục không được để trống")
    private Integer maDanhMuc;

    @NotNull(message = "Mã thương hiệu không được để trống")
    private Integer maThuongHieu;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String tenSanPham;

    private String moTa;

    // Giá gốc: Đã chuyển sang quản lý ở bảng CHI_TIET_SAN_PHAM nên cho phép = 0
    @DecimalMin(value = "0.0", inclusive = true, message = "Giá gốc phải lớn hơn hoặc bằng 0")
    private BigDecimal giaGoc;

    private BigDecimal giaKhuyenMai;

    private String trangThai;
}

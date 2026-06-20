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

    @NotNull(message = "Giá gốc không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá gốc phải lớn hơn 0")
    private BigDecimal giaGoc;

    @NotNull(message = "Giá khuyến mãi không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá khuyến mãi phải lớn hơn 0")
    private BigDecimal giaKhuyenMai;
}

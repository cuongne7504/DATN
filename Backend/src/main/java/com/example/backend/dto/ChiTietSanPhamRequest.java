package com.example.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ChiTietSanPhamRequest {

    @NotNull(message = "Mã sản phẩm không được để trống")
    private Integer maSanPham;

    @NotBlank(message = "Mã vạch SKU không được để trống")
    private String maVachSku;

    @NotBlank(message = "Màu sắc không được để trống")
    private String mauSac;

    @NotBlank(message = "Kích cỡ không được để trống")
    private String kichCo;

    @NotNull(message = "Số lượng tồn không được để trống")
    @Min(value = 0, message = "Số lượng tồn phải lớn hơn hoặc bằng 0")
    private Integer soLuongTon;

    @NotNull(message = "Giá cộng thêm không được để trống")
    @DecimalMin(value = "0.0", inclusive = true, message = "Giá cộng thêm phải lớn hơn hoặc bằng 0")
    private BigDecimal giaCongThem;
}

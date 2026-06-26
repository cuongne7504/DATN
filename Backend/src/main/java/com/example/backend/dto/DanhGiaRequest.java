package com.example.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DanhGiaRequest {

    @NotNull(message = "Mã người dùng không được để trống")
    private Integer maNguoiDung;

    @NotNull(message = "Mã sản phẩm không được để trống")
    private Integer maSanPham;

    @NotNull(message = "Số sao không được để trống")
    @Min(value = 1, message = "Số sao tối thiểu là 1")
    @Max(value = 5, message = "Số sao tối đa là 5")
    private Integer soSao;

    private String noiDung;
}

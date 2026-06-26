package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HinhAnhSpRequest {

    @NotNull(message = "Mã sản phẩm không được để trống")
    private Integer maSanPham;

    @NotBlank(message = "Đường dẫn ảnh không được để trống")
    private String duongDanAnh;

    private Boolean laAnhChinh;
}

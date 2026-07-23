package com.example.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class KhuyenMaiRequest {

    @NotBlank(message = "Mã code không được để trống")
    private String maCode;

    private String moTa;

    @Min(value = 0, message = "Phần trăm giảm phải >= 0")
    private Integer phanTramGiam;

    private BigDecimal soTienGiam;

    private BigDecimal donToiThieu;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    private LocalDateTime ngayBatDau;

    @NotNull(message = "Ngày kết thúc không được để trống")
    private LocalDateTime ngayKetThuc;

    @Min(value = 1, message = "Số lượng dùng phải >= 1")
    private Integer soLuongDung;
}

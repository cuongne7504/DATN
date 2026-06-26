package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfitReportItemDto {
    private Integer maDonHang;
    private LocalDateTime ngayDat;
    private String tenSanPham;
    private String kichCo;
    private String mauSac;
    private Integer soLuong;
    private BigDecimal donGiaBan; // Selling price per item in the order
    private BigDecimal giaGoc;     // Original cost price of the product
    private BigDecimal loiNhuanTrenMotSp; // Profit per item
    private BigDecimal tongLoiNhuanItem; // Total profit for this item in the order
}

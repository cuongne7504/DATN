package com.example.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "KHUYEN_MAI")
@Getter
@Setter
public class KhuyenMai {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_khuyen_mai")
    private Integer maKhuyenMai;

    @Column(name = "ma_code")
    private String maCode;

    @Column(name = "phan_tram_giam")
    private Integer phanTramGiam;

    @Column(name = "so_tien_giam")
    private BigDecimal soTienGiam;

    @Column(name = "don_toi_thieu")
    private BigDecimal donToiThieu;

    @Column(name = "ngay_bat_dau")
    private LocalDateTime ngayBatDau;

    @Column(name = "ngay_ket_thuc")
    private LocalDateTime ngayKetThuc;

    @Column(name = "so_luong_dung")
    private Integer soLuongDung;
}



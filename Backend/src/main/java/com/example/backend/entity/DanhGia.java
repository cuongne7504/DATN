package com.example.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "DANH_GIA")
@Getter
@Setter
public class DanhGia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_danh_gia")
    private Integer maDanhGia;

    @Column(name = "ma_nguoi_dung")
    private Integer maNguoiDung;

    @Column(name = "ma_san_pham")
    private Integer maSanPham;

    @Column(name = "so_sao")
    private Integer soSao;

    @Column(name = "noi_dung")
    private String noiDung;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;
}



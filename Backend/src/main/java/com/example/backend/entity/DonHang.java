package com.example.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "DON_HANG")
@Getter
@Setter
public class DonHang {

    @Id
    @Column(name = "ma_don_hang")
    private Integer maDonHang;

    @Column(name = "ma_nguoi_dung")
    private Integer maNguoiDung;

    @Column(name = "ma_nhan_vien")
    private Integer maNhanVien;

    @Column(name = "ma_khuyen_mai")
    private Integer maKhuyenMai;

    @Column(name = "ngay_dat")
    private LocalDateTime ngayDat;

    @Column(name = "tong_tien")
    private BigDecimal tongTien;

    @Column(name = "phi_ship")
    private BigDecimal phiShip;

    @Column(name = "dia_chi_giao")
    private String diaChiGiao;

    @Column(name = "phuong_thuc_tt")
    private String phuongThucTt;

    @Column(name = "trang_thai")
    private String trangThai;
}

package com.example.backend.entity;

import jakarta.persistence.*;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Column(name = "shipper_name")
    private String shipperName;

    @Column(name = "shipper_phone")
    private String shipperPhone;

    @Column(name = "shipping_note")
    private String shippingNote;

    @Column(name = "shipping_code")
    private String shippingCode;
}



package com.example.backend.dto;

import com.example.backend.entity.ChiTietDonHang;
import com.example.backend.entity.DonHang;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class DonHangDetailResponse {

    private final Integer maDonHang;
    private final Integer maNguoiDung;
    private final Integer maNhanVien;
    private final Integer maKhuyenMai;
    private final LocalDateTime ngayDat;
    private final BigDecimal tongTien;
    private final BigDecimal phiShip;
    private final String diaChiGiao;
    private final String phuongThucTt;
    private final String trangThai;
    private final List<ChiTietDonHang> chiTietList;

    public DonHangDetailResponse(DonHang donHang, List<ChiTietDonHang> chiTietList) {
        this.maDonHang = donHang.getMaDonHang();
        this.maNguoiDung = donHang.getMaNguoiDung();
        this.maNhanVien = donHang.getMaNhanVien();
        this.maKhuyenMai = donHang.getMaKhuyenMai();
        this.ngayDat = donHang.getNgayDat();
        this.tongTien = donHang.getTongTien();
        this.phiShip = donHang.getPhiShip();
        this.diaChiGiao = donHang.getDiaChiGiao();
        this.phuongThucTt = donHang.getPhuongThucTt();
        this.trangThai = donHang.getTrangThai();
        this.chiTietList = chiTietList;
    }
}

package com.example.backend.dto;

import com.example.backend.entity.ChiTietDonHang;
import com.example.backend.entity.DonHang;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import com.example.backend.entity.KhuyenMai;

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
    private final String shipperName;
    private final String shipperPhone;
    private final String shippingNote;
    private final String shippingCode;
    private final KhuyenMai khuyenMai;
    private final List<ChiTietDonHangDto> chiTietList;

    public DonHangDetailResponse(DonHang donHang, List<ChiTietDonHangDto> chiTietList, KhuyenMai khuyenMai) {
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
        this.shipperName = donHang.getShipperName();
        this.shipperPhone = donHang.getShipperPhone();
        this.shippingNote = donHang.getShippingNote();
        this.shippingCode = donHang.getShippingCode();
        this.khuyenMai = khuyenMai;
        this.chiTietList = chiTietList;
    }

    @Getter
    public static class ChiTietDonHangDto {
        private Integer maCtDonHang;
        private Integer maDonHang;
        private Integer maChiTietSp;
        private Integer soLuong;
        private BigDecimal donGia;
        private com.example.backend.entity.ChiTietSanPham chiTietSanPham;
        private com.example.backend.entity.SanPham sanPham;

        public ChiTietDonHangDto(ChiTietDonHang ct, com.example.backend.entity.ChiTietSanPham chiTietSanPham, com.example.backend.entity.SanPham sanPham) {
            this.maCtDonHang = ct.getMaCtDonHang();
            this.maDonHang = ct.getMaDonHang();
            this.maChiTietSp = ct.getMaChiTietSp();
            this.soLuong = ct.getSoLuong();
            this.donGia = ct.getDonGia();
            this.chiTietSanPham = chiTietSanPham;
            this.sanPham = sanPham;
        }
    }
}

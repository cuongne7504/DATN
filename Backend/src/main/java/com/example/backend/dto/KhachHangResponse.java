package com.example.backend.dto;

import com.example.backend.entity.KhachHang;
import lombok.Getter;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class KhachHangResponse {
    private final Integer maKhachHang;
    private final Integer maNguoiDung;
    private final String hoTen;
    private final String tenKhachHang;
    private final String email;
    private final String soDienThoai;
    private final String diaChi;
    private final String gioiTinh;
    private final LocalDate ngaySinh;
    private final Boolean trangThai;
    private final LocalDateTime ngayTao;

    public KhachHangResponse(KhachHang kh) {
        this.maKhachHang = kh.getMaKhachHang();
        if (kh.getNguoiDung() != null) {
            this.maNguoiDung = kh.getNguoiDung().getMaNguoiDung();
            this.hoTen = kh.getNguoiDung().getHoTen();
            this.email = kh.getNguoiDung().getEmail();
            this.soDienThoai = kh.getNguoiDung().getSoDienThoai();
            this.diaChi = kh.getNguoiDung().getDiaChi();
            this.ngayTao = kh.getNguoiDung().getNgayTao();
            this.tenKhachHang = kh.getTenKhachHang() != null ? kh.getTenKhachHang() : this.hoTen;
        } else {
            this.maNguoiDung = null;
            this.hoTen = null;
            this.email = null;
            this.soDienThoai = null;
            this.diaChi = null;
            this.ngayTao = null;
            this.tenKhachHang = kh.getTenKhachHang();
        }
        this.gioiTinh = kh.getGioiTinh();
        this.ngaySinh = kh.getNgaySinh();
        this.trangThai = kh.getTrangThai();
    }
}

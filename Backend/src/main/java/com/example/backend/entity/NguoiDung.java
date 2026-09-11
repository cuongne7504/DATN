package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "NGUOI_DUNG")
@Getter
@Setter
public class NguoiDung {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_nguoi_dung")
    private Integer maNguoiDung;
    @Column(name = "ma_quyen")
    private Integer maQuyen;

    @Column(name = "ho_ten")
    private String hoTen;

    @Column(name = "email")
    private String email;

    @Column(name = "mat_khau")
    private String matKhau;

    @Column(name = "so_dien_thoai")
    private String soDienThoai;

    @Column(name = "dia_chi")
    private String diaChi;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @Column(name = "trang_thai")
    private String trangThai = "Hoạt động";
}

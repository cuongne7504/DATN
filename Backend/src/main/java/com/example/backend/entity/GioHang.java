package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "GIO_HANG")
@Getter
@Setter
public class GioHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_gio_hang")
    private Integer maGioHang;

    @Column(name = "ma_nguoi_dung")
    private Integer maNguoiDung;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao = LocalDateTime.now();
}

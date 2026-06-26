package com.example.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "GIO_HANG")
@Getter
@Setter
public class GioHang {

    @Id
    @Column(name = "ma_gio_hang")
    private Integer maGioHang;

    @Column(name = "ma_nguoi_dung")
    private Integer maNguoiDung;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;
}

package com.example.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "CHI_TIET_GIO_HANG")
@Getter
@Setter
public class ChiTietGioHang {

    @Id
    @Column(name = "ma_ct_gio_hang")
    private Integer maCtGioHang;

    @Column(name = "ma_gio_hang")
    private Integer maGioHang;

    @Column(name = "ma_chi_tiet_sp")
    private Integer maChiTietSp;

    @Column(name = "so_luong")
    private Integer soLuong;
}

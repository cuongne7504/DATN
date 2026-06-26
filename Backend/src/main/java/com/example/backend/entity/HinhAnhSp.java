package com.example.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "HINH_ANH_SP")
@Getter
@Setter
public class HinhAnhSp {

    @Id
    @Column(name = "ma_hinh_anh")
    private Integer maHinhAnh;

    @Column(name = "ma_san_pham")
    private Integer maSanPham;

    @Column(name = "duong_dan_anh")
    private String duongDanAnh;

    @Column(name = "la_anh_chinh")
    private Boolean laAnhChinh;
}

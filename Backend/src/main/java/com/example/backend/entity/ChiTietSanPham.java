package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "CHI_TIET_SAN_PHAM")
@Getter
@Setter
public class ChiTietSanPham {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_chi_tiet_sp")
    private Integer maChiTietSp;

    @Column(name = "ma_san_pham")
    private Integer maSanPham;

    @Column(name = "ma_vach_sku")
    private String maVachSku;

    @Column(name = "mau_sac")
    private String mauSac;

    @Column(name = "kich_co")
    private String kichCo;

    @Column(name = "so_luong_ton")
    private Integer soLuongTon;

    @Column(name = "gia_cong_them")
    private BigDecimal giaCongThem;
}

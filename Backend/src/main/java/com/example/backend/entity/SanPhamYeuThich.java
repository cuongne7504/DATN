package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "SAN_PHAM_YEU_THICH")
@Getter
@Setter
public class SanPhamYeuThich {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_yeu_thich")
    private Integer maYeuThich;

    @Column(name = "ma_nguoi_dung")
    private Integer maNguoiDung;

    @ManyToOne
    @JoinColumn(name = "ma_san_pham", referencedColumnName = "ma_san_pham")
    private SanPham sanPham;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;
}

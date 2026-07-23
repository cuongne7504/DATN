package com.example.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "YEU_CAU_HOAN_HANG")
@Getter
@Setter
public class YeuCauHoanHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_yeu_cau")
    private Integer maYeuCau;

    @Column(name = "ma_don_hang")
    private Integer maDonHang;

    @Column(name = "ly_do")
    private String lyDo;

    @Column(name = "hinh_anh_minh_hoa")
    private String hinhAnhMinhHoa;

    @Column(name = "so_tien_hoan")
    private BigDecimal soTienHoan;

    @Column(name = "trang_thai")
    private String trangThai;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;
}

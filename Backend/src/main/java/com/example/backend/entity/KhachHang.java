package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "KHACH_HANG")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KhachHang {

    @Id
    @Column(name = "ma_khach_hang")
    private Integer maKhachHang;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ma_nguoi_dung", referencedColumnName = "ma_nguoi_dung", nullable = false)
    private NguoiDung nguoiDung;

    @Transient
    private String tenKhachHang;

    @Column(name = "gioi_tinh", columnDefinition = "NVARCHAR(10)")
    private String gioiTinh;

    @Column(name = "ngay_sinh")
    private LocalDate ngaySinh;

    @Column(name = "trang_thai", nullable = false)
    private Boolean trangThai = true;
}

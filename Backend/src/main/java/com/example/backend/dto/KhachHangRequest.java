package com.example.backend.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class KhachHangRequest {
    private String hoTen;
    private String email;
    private String matKhau;
    private String tenKhachHang;
    private String soDienThoai;
    private String diaChi;
    private String gioiTinh;
    private LocalDate ngaySinh;
    private Boolean trangThai;
}

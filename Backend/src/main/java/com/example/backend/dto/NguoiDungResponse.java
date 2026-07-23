package com.example.backend.dto;

import com.example.backend.entity.NguoiDung;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class NguoiDungResponse {

    private Integer maNguoiDung;
    private Integer maQuyen;
    private String hoTen;
    private String email;
    private String soDienThoai;
    private String diaChi;
    private LocalDateTime ngayTao;

    public NguoiDungResponse(NguoiDung nguoiDung) {
        this.maNguoiDung = nguoiDung.getMaNguoiDung();
        this.maQuyen = nguoiDung.getMaQuyen();
        this.hoTen = nguoiDung.getHoTen();
        this.email = nguoiDung.getEmail();
        this.soDienThoai = nguoiDung.getSoDienThoai();
        this.diaChi = nguoiDung.getDiaChi();
        this.ngayTao = nguoiDung.getNgayTao();
    }
}

package com.example.backend.dto;

import com.example.backend.entity.NguoiDung;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NguoiDungResponse {

    private final Integer maNguoiDung;
    private final Integer maQuyen;
    private final String hoTen;
    private final String email;
    private final String soDienThoai;
    private final String diaChi;
    private final LocalDateTime ngayTao;

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

package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GioHangDetailResponse {

    private Integer maGioHang;
    private Integer maNguoiDung;
    private List<GioHangItemDetail> items;
    private BigDecimal tongTien;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class GioHangItemDetail {
        private Integer maCtGioHang;
        private Integer maChiTietSp;
        private String maVachSku;
        private String tenSanPham;
        private String mauSac;
        private String kichCo;
        private Integer soLuong;
        private BigDecimal giaBan; // giaKhuyenMai + giaCongThem
        private BigDecimal thanhTien; // giaBan * soLuong
    }
}

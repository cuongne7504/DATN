package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TonKhoResponse {
    private Integer maChiTietSp;
    private String tenSanPham;
    private String maVachSku;
    private String mauSac;
    private String kichCo;
    private Integer soLuongTon;
}

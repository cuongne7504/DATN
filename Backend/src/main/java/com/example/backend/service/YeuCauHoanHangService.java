package com.example.backend.service;

import com.example.backend.dto.YeuCauHoanHangRequest;
import com.example.backend.entity.YeuCauHoanHang;
import java.util.List;

public interface YeuCauHoanHangService {
    YeuCauHoanHang taoYeuCau(YeuCauHoanHangRequest request);
    List<YeuCauHoanHang> getTatCaYeuCau();
    List<YeuCauHoanHang> getYeuCauTheoDonHang(Integer maDonHang);
    YeuCauHoanHang capNhatTrangThai(Integer maYeuCau, String trangThaiMoi);
}

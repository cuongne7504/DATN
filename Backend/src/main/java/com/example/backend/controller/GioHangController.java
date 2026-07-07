package com.example.backend.controller;

import com.example.backend.dto.ApiResponse;
import com.example.backend.entity.ChiTietGioHang;
import com.example.backend.service.GioHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/gio-hang")
@CrossOrigin(origins = "*")
public class GioHangController {

    @Autowired
    private GioHangService gioHangService;

    @GetMapping("/cua-toi/{maNguoiDung}")
    public ApiResponse<List<ChiTietGioHang>> getGioHang(@PathVariable Integer maNguoiDung) {
        return ApiResponse.ok("Lấy giỏ hàng thành công", gioHangService.getChiTietGioHang(maNguoiDung));
    }

    @PostMapping("/them/{maNguoiDung}")
    public ApiResponse<ChiTietGioHang> themVaoGio(@PathVariable Integer maNguoiDung, @RequestBody Map<String, Integer> payload) {
        Integer maChiTietSp = payload.get("maChiTietSp");
        Integer soLuong = payload.get("soLuong");
        ChiTietGioHang saved = gioHangService.themVaoGio(maNguoiDung, maChiTietSp, soLuong);
        return ApiResponse.ok("Đã thêm vào giỏ hàng", saved);
    }

    @PutMapping("/cap-nhat/{maCtGioHang}")
    public ApiResponse<ChiTietGioHang> capNhat(@PathVariable Integer maCtGioHang, @RequestBody Map<String, Integer> payload) {
        Integer soLuong = payload.get("soLuong");
        ChiTietGioHang updated = gioHangService.capNhatSoLuong(maCtGioHang, soLuong);
        return ApiResponse.ok("Cập nhật số lượng thành công", updated);
    }

    @DeleteMapping("/xoa/{maCtGioHang}")
    public ApiResponse<String> xoa(@PathVariable Integer maCtGioHang) {
        gioHangService.xoaKhoiGio(maCtGioHang);
        return ApiResponse.ok("Đã xóa khỏi giỏ hàng", null);
    }
}

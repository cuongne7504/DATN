package com.example.backend.controller;

import com.example.backend.dto.ApiResponse;
import com.example.backend.dto.YeuCauHoanHangRequest;
import com.example.backend.entity.YeuCauHoanHang;
import com.example.backend.service.YeuCauHoanHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hoan-hang")
@CrossOrigin(origins = "*")
public class YeuCauHoanHangController {

    @Autowired
    private YeuCauHoanHangService hoanHangService;

    @PostMapping
    public ResponseEntity<ApiResponse<YeuCauHoanHang>> taoYeuCau(@RequestBody YeuCauHoanHangRequest request) {
        YeuCauHoanHang yeuCau = hoanHangService.taoYeuCau(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Tạo yêu cầu hoàn hàng thành công", yeuCau));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<YeuCauHoanHang>>> getTatCaYeuCau() {
        List<YeuCauHoanHang> list = hoanHangService.getTatCaYeuCau();
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách yêu cầu hoàn hàng thành công", list));
    }

    @GetMapping("/don-hang/{maDonHang}")
    public ResponseEntity<ApiResponse<List<YeuCauHoanHang>>> getYeuCauTheoDonHang(@PathVariable Integer maDonHang) {
        List<YeuCauHoanHang> list = hoanHangService.getYeuCauTheoDonHang(maDonHang);
        return ResponseEntity.ok(new ApiResponse<>(true, "Thành công", list));
    }

    @PutMapping("/{maYeuCau}/trang-thai")
    public ResponseEntity<ApiResponse<YeuCauHoanHang>> capNhatTrangThai(
            @PathVariable Integer maYeuCau,
            @RequestParam String trangThaiMoi) {
        YeuCauHoanHang yeuCau = hoanHangService.capNhatTrangThai(maYeuCau, trangThaiMoi);
        return ResponseEntity.ok(new ApiResponse<>(true, "Cập nhật trạng thái thành công", yeuCau));
    }
}

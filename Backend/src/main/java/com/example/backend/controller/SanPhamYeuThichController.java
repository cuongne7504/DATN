package com.example.backend.controller;

import com.example.backend.dto.ApiResponse;
import com.example.backend.entity.SanPhamYeuThich;
import com.example.backend.service.SanPhamYeuThichService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/yeu-thich")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SanPhamYeuThichController {

    private final SanPhamYeuThichService yeuThichService;

    @GetMapping("/nguoi-dung/{maNguoiDung}")
    public ResponseEntity<ApiResponse<List<SanPhamYeuThich>>> getByMaNguoiDung(@PathVariable Integer maNguoiDung) {
        return ResponseEntity.ok(ApiResponse.ok(yeuThichService.getByMaNguoiDung(maNguoiDung)));
    }

    @PostMapping("/toggle")
    public ResponseEntity<ApiResponse<SanPhamYeuThich>> toggle(
            @RequestParam Integer maNguoiDung,
            @RequestParam Integer maSanPham) {
        SanPhamYeuThich yt = yeuThichService.toggle(maNguoiDung, maSanPham);
        return ResponseEntity.ok(ApiResponse.ok("Thanh cong", yt));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        yeuThichService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Thanh cong", null));
    }
}

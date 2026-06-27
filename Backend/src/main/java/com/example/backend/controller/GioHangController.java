package com.example.backend.controller;

import com.example.backend.dto.ApiResponse;
import com.example.backend.dto.GioHangDetailResponse;
import com.example.backend.dto.GioHangItemRequest;
import com.example.backend.entity.ChiTietGioHang;
import com.example.backend.service.GioHangService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/gio-hang")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GioHangController {

    private final GioHangService gioHangService;

    // --- CÁC ENDPOINT TỪ NHÁNH KY (Frontend chính đang gọi) ---

    @GetMapping("/{maNguoiDung}")
    public ResponseEntity<ApiResponse<GioHangDetailResponse>> getCartDetail(@PathVariable Integer maNguoiDung) {
        return ResponseEntity.ok(ApiResponse.ok(gioHangService.getCartDetail(maNguoiDung)));
    }

    @PostMapping("/{maNguoiDung}/items")
    public ResponseEntity<ApiResponse<GioHangDetailResponse>> addItem(
            @PathVariable Integer maNguoiDung,
            @Valid @RequestBody GioHangItemRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(gioHangService.addItem(maNguoiDung, request)));
    }

    @PutMapping("/{maNguoiDung}/items/{maCtGioHang}")
    public ResponseEntity<ApiResponse<GioHangDetailResponse>> updateItem(
            @PathVariable Integer maNguoiDung,
            @PathVariable Integer maCtGioHang,
            @RequestParam Integer soLuong) {
        return ResponseEntity.ok(ApiResponse.ok(gioHangService.updateItem(maNguoiDung, maCtGioHang, soLuong)));
    }

    @DeleteMapping("/{maNguoiDung}/items/{maCtGioHang}")
    public ResponseEntity<ApiResponse<GioHangDetailResponse>> removeItem(
            @PathVariable Integer maNguoiDung,
            @PathVariable Integer maCtGioHang) {
        return ResponseEntity.ok(ApiResponse.ok(gioHangService.removeItem(maNguoiDung, maCtGioHang)));
    }

    @DeleteMapping("/{maNguoiDung}")
    public ResponseEntity<ApiResponse<Void>> clearCart(@PathVariable Integer maNguoiDung) {
        gioHangService.clearCart(maNguoiDung);
        return ResponseEntity.ok(ApiResponse.ok("Xóa giỏ hàng thành công", null));
    }

    // --- CÁC ENDPOINT TỪ NHÁNH MAIN ---

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

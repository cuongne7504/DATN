package com.example.backend.controller;

import com.example.backend.dto.ApiResponse;
import com.example.backend.dto.ChiTietSanPhamRequest;
import com.example.backend.dto.TonKhoResponse;
import com.example.backend.entity.ChiTietSanPham;
import com.example.backend.service.ChiTietSanPhamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChiTietSanPhamController {

    private final ChiTietSanPhamService chiTietSanPhamService;

    // --- API nhánh ky (/api/biens-the) ---

    @GetMapping("/api/biens-the")
    public ResponseEntity<ApiResponse<List<ChiTietSanPham>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(chiTietSanPhamService.getAll()));
    }

    @GetMapping("/api/biens-the/{id}")
    public ResponseEntity<ApiResponse<ChiTietSanPham>> getByIdKy(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok(chiTietSanPhamService.getById(id)));
    }

    @GetMapping("/api/biens-the/san-pham/{maSanPham}")
    public ResponseEntity<ApiResponse<List<ChiTietSanPham>>> getByMaSanPhamKy(@PathVariable Integer maSanPham) {
        return ResponseEntity.ok(ApiResponse.ok(chiTietSanPhamService.getByMaSanPham(maSanPham)));
    }

    @PostMapping("/api/biens-the")
    public ResponseEntity<ApiResponse<ChiTietSanPham>> create(@Valid @RequestBody ChiTietSanPhamRequest request) {
        ChiTietSanPham created = chiTietSanPhamService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Thêm biến thể sản phẩm thành công", created));
    }

    @PutMapping("/api/biens-the/{id}")
    public ResponseEntity<ApiResponse<ChiTietSanPham>> update(
            @PathVariable Integer id,
            @Valid @RequestBody ChiTietSanPhamRequest request) {
        ChiTietSanPham updated = chiTietSanPhamService.update(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Cập nhật biến thể sản phẩm thành công", updated));
    }

    @DeleteMapping("/api/biens-the/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteKy(@PathVariable Integer id) {
        chiTietSanPhamService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Xóa biến thể sản phẩm thành công", null));
    }

    // --- API nhánh main (/api/chi-tiet-san-pham) ---

    @GetMapping("/api/chi-tiet-san-pham/san-pham/{maSanPham}")
    public ApiResponse<List<ChiTietSanPham>> getBySanPham(@PathVariable Integer maSanPham) {
        List<ChiTietSanPham> list = chiTietSanPhamService.getByMaSanPham(maSanPham);
        return ApiResponse.ok("Lấy danh sách biến thể thành công", list);
    }

    @GetMapping("/api/chi-tiet-san-pham/ton-kho")
    public ApiResponse<List<TonKhoResponse>> getTonKho() {
        List<TonKhoResponse> list = chiTietSanPhamService.layDanhSachTonKho();
        return ApiResponse.ok("Lấy danh sách tồn kho thành công", list);
    }

    @GetMapping("/api/chi-tiet-san-pham/{id}")
    public ApiResponse<ChiTietSanPham> getById(@PathVariable Integer id) {
        ChiTietSanPham chiTiet = chiTietSanPhamService.getById(id);
        return ApiResponse.ok("Lấy biến thể thành công", chiTiet);
    }

    @GetMapping("/api/chi-tiet-san-pham/sku/{sku}")
    public ApiResponse<ChiTietSanPham> getBySku(@PathVariable String sku) {
        ChiTietSanPham chiTiet = chiTietSanPhamService.getByMaVachSku(sku);
        return ApiResponse.ok("Tìm biến thể bằng SKU thành công", chiTiet);
    }

    @PostMapping("/api/chi-tiet-san-pham")
    public ApiResponse<ChiTietSanPham> themMoi(@RequestBody ChiTietSanPham chiTiet) {
        ChiTietSanPham saved = chiTietSanPhamService.themMoi(chiTiet);
        return ApiResponse.ok("Thêm biến thể thành công! Đã cấp mã vạch: " + saved.getMaVachSku(), saved);
    }

    @PutMapping("/api/chi-tiet-san-pham/{id}")
    public ApiResponse<ChiTietSanPham> capNhat(@PathVariable Integer id, @RequestBody ChiTietSanPham chiTiet) {
        ChiTietSanPham updated = chiTietSanPhamService.capNhat(id, chiTiet);
        return ApiResponse.ok("Cập nhật biến thể thành công", updated);
    }

    @DeleteMapping("/api/chi-tiet-san-pham/{id}")
    public ApiResponse<String> xoa(@PathVariable Integer id) {
        chiTietSanPhamService.xoa(id);
        return ApiResponse.ok("Xóa biến thể thành công", null);
    }

    // Quét POS: tìm theo ID hoặc SKU
    @GetMapping("/api/chi-tiet-san-pham/tra-cuu/{skuOrId}")
    public ApiResponse<ChiTietSanPham> getBySkuOrId(@PathVariable String skuOrId) {
        try {
            Integer id = Integer.parseInt(skuOrId);
            return ApiResponse.ok("Lấy chi tiết sản phẩm thành công", chiTietSanPhamService.getById(id));
        } catch (NumberFormatException e) {
            return ApiResponse.ok("Lấy chi tiết sản phẩm thành công", chiTietSanPhamService.getByMaVachSku(skuOrId));
        }
    }
}

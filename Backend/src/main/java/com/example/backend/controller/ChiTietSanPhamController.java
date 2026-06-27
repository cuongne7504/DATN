package com.example.backend.controller;

import com.example.backend.dto.ApiResponse;
import com.example.backend.dto.ChiTietSanPhamRequest;
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

    // --- CÁC MAPPING TỪ NHÁNH KY (/api/biens-the) ---

    @GetMapping("/api/biens-the")
    public ResponseEntity<ApiResponse<List<ChiTietSanPham>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(chiTietSanPhamService.getAll()));
    }

    @GetMapping("/api/biens-the/{id}")
    public ResponseEntity<ApiResponse<ChiTietSanPham>> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok(chiTietSanPhamService.getById(id)));
    }

    @GetMapping("/api/biens-the/san-pham/{maSanPham}")
    public ResponseEntity<ApiResponse<List<ChiTietSanPham>>> getByMaSanPham(@PathVariable Integer maSanPham) {
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
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        chiTietSanPhamService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Xóa biến thể sản phẩm thành công", null));
    }

    // --- CÁC MAPPING TỪ NHÁNH MAIN/CUONG (/api/chi-tiet-san-pham) ---

    // Xem danh sách các size/màu của 1 sản phẩm
    @GetMapping("/api/chi-tiet-san-pham/san-pham/{maSanPham}")
    public ApiResponse<List<ChiTietSanPham>> getBySanPham(@PathVariable Integer maSanPham) {
        List<ChiTietSanPham> list = chiTietSanPhamService.getByMaSanPham(maSanPham);
        return ApiResponse.ok("Lấy danh sách biến thể thành công", list);
    }

    // Thêm 1 màu/size mới (Tự sinh mã vạch)
    @PostMapping("/api/chi-tiet-san-pham")
    public ApiResponse<ChiTietSanPham> themMoi(@RequestBody ChiTietSanPham chiTiet) {
        ChiTietSanPham saved = chiTietSanPhamService.themMoi(chiTiet);
        return ApiResponse.ok("Thêm biến thể thành công! Đã cấp mã vạch: " + saved.getMaVachSku(), saved);
    }

    // Cập nhật tồn kho/giá
    @PutMapping("/api/chi-tiet-san-pham/{id}")
    public ApiResponse<ChiTietSanPham> capNhat(@PathVariable Integer id, @RequestBody ChiTietSanPham chiTiet) {
        ChiTietSanPham updated = chiTietSanPhamService.capNhat(id, chiTiet);
        return ApiResponse.ok("Cập nhật biến thể thành công", updated);
    }

    // Xóa biến thể
    @DeleteMapping("/api/chi-tiet-san-pham/{id}")
    public ApiResponse<String> xoa(@PathVariable Integer id) {
        chiTietSanPhamService.xoa(id);
        return ApiResponse.ok("Xóa biến thể thành công", null);
    }

    // Tìm kiếm biến thể bằng mã vạch SKU hoặc ID (phục vụ quét POS)
    @GetMapping("/api/chi-tiet-san-pham/{skuOrId}")
    public ApiResponse<ChiTietSanPham> getBySkuOrId(@PathVariable String skuOrId) {
        try {
            // Thử parse thành số để tìm theo ID trước
            Integer id = Integer.parseInt(skuOrId);
            return ApiResponse.ok("Lấy chi tiết sản phẩm thành công", chiTietSanPhamService.getById(id));
        } catch (NumberFormatException e) {
            // Nếu không phải số, tìm theo SKU
            return chiTietSanPhamService.getAll().stream()
                    .filter(v -> skuOrId.equals(v.getMaVachSku()))
                    .findFirst()
                    .map(v -> ApiResponse.ok("Lấy chi tiết sản phẩm thành công", v))
                    .orElseGet(() -> ApiResponse.fail("Không tìm thấy sản phẩm có mã này"));
        }
    }
}

package com.example.backend.repository;

import com.example.backend.entity.ChiTietSanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChiTietSanPhamRepository extends JpaRepository<ChiTietSanPham, Integer> {
    
    // Tìm danh sách chi tiết sản phẩm theo mã sản phẩm
    List<ChiTietSanPham> findByMaSanPham(Integer maSanPham);

    // Kiểm tra mã vạch SKU đã tồn tại chưa
    boolean existsByMaVachSku(String maVachSku);
    
    // Tìm chi tiết sản phẩm theo mã vạch SKU
    Optional<ChiTietSanPham> findByMaVachSku(String maVachSku);

    @org.springframework.data.jpa.repository.Query("SELECT new com.example.backend.dto.TonKhoResponse(c.maChiTietSp, s.tenSanPham, c.maVachSku, c.mauSac, c.kichCo, c.soLuongTon) FROM ChiTietSanPham c JOIN SanPham s ON c.maSanPham = s.maSanPham ORDER BY c.soLuongTon ASC")
    List<com.example.backend.dto.TonKhoResponse> layDanhSachTonKho();
}

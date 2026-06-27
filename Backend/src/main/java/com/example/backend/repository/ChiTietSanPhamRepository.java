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
}

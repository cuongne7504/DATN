package com.example.backend.repository;

import com.example.backend.entity.ChiTietSanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChiTietSanPhamRepository extends JpaRepository<ChiTietSanPham, Integer> {
    
    List<ChiTietSanPham> findByMaSanPham(Integer maSanPham);
    
    Optional<ChiTietSanPham> findByMaVachSku(String maVachSku);
}

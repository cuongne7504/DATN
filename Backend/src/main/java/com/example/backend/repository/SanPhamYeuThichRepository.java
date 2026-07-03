package com.example.backend.repository;

import com.example.backend.entity.SanPhamYeuThich;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SanPhamYeuThichRepository extends JpaRepository<SanPhamYeuThich, Integer> {
    List<SanPhamYeuThich> findByMaNguoiDung(Integer maNguoiDung);
    Optional<SanPhamYeuThich> findByMaNguoiDungAndSanPham_MaSanPham(Integer maNguoiDung, Integer maSanPham);
}

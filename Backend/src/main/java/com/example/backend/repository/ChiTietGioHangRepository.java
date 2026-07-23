package com.example.backend.repository;

import com.example.backend.entity.ChiTietGioHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChiTietGioHangRepository extends JpaRepository<ChiTietGioHang, Integer> {
    List<ChiTietGioHang> findByMaGioHang(Integer maGioHang);
    Optional<ChiTietGioHang> findByMaGioHangAndMaChiTietSp(Integer maGioHang, Integer maChiTietSp);
    void deleteByMaGioHang(Integer maGioHang);
}

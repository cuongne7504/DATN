package com.example.backend.repository;

import com.example.backend.entity.YeuCauHoanHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YeuCauHoanHangRepository extends JpaRepository<YeuCauHoanHang, Integer> {
    List<YeuCauHoanHang> findByMaDonHang(Integer maDonHang);
}

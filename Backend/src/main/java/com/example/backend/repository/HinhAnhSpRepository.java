package com.example.backend.repository;

import com.example.backend.entity.HinhAnhSp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HinhAnhSpRepository extends JpaRepository<HinhAnhSp, Integer> {
    
    List<HinhAnhSp> findByMaSanPham(Integer maSanPham);
    
    Optional<HinhAnhSp> findByMaSanPhamAndLaAnhChinh(Integer maSanPham, Boolean laAnhChinh);
}

package com.example.backend.repository;

import com.example.backend.entity.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KhachHangRepository extends JpaRepository<KhachHang, Integer> {

    @Query("SELECT k FROM KhachHang k WHERE k.nguoiDung.email = :email")
    Optional<KhachHang> findByEmail(@Param("email") String email);

    @Query("SELECT k FROM KhachHang k LEFT JOIN FETCH k.nguoiDung ORDER BY k.maKhachHang DESC")
    List<KhachHang> findAllWithNguoiDung();

    @Query("SELECT k FROM KhachHang k LEFT JOIN FETCH k.nguoiDung WHERE " +
           "LOWER(COALESCE(k.nguoiDung.hoTen, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(COALESCE(k.nguoiDung.email, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(COALESCE(k.nguoiDung.soDienThoai, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "ORDER BY k.maKhachHang DESC")
    List<KhachHang> searchByKeywordWithNguoiDung(@Param("keyword") String keyword);

    @Query("SELECT COALESCE(MAX(k.maKhachHang), 0) FROM KhachHang k")
    Integer findMaxId();
}

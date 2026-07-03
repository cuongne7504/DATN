package com.example.backend.repository;

import com.example.backend.entity.SanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface SanPhamRepository extends JpaRepository<SanPham, Integer> {

    @Query("SELECT DISTINCT s FROM SanPham s " +
           "LEFT JOIN ChiTietSanPham c ON s.maSanPham = c.maSanPham " +
           "WHERE (:ten IS NULL OR LOWER(s.tenSanPham) LIKE LOWER(CONCAT('%', :ten, '%'))) " +
           "AND (:maDanhMuc IS NULL OR s.maDanhMuc = :maDanhMuc) " +
           "AND (:maThuongHieu IS NULL OR s.maThuongHieu = :maThuongHieu) " +
           "AND (:minGia IS NULL OR s.giaKhuyenMai >= :minGia) " +
           "AND (:maxGia IS NULL OR s.giaKhuyenMai <= :maxGia) " +
           "AND (:kichCo IS NULL OR c.kichCo = :kichCo) " +
           "AND (:mauSac IS NULL OR c.mauSac = :mauSac)")
    List<SanPham> searchProducts(
        @Param("ten") String ten,
        @Param("maDanhMuc") Integer maDanhMuc,
        @Param("maThuongHieu") Integer maThuongHieu,
        @Param("minGia") BigDecimal minGia,
        @Param("maxGia") BigDecimal maxGia,
        @Param("kichCo") String kichCo,
        @Param("mauSac") String mauSac
    );
}


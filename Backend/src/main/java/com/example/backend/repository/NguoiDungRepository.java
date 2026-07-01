package com.example.backend.repository;

import com.example.backend.entity.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NguoiDungRepository extends JpaRepository<NguoiDung, Integer> {
    Optional<NguoiDung> findByEmail(String email);

    @Query("SELECT COALESCE(MAX(n.maNguoiDung), 0) FROM NguoiDung n")
    Integer findMaxId();
}

package com.example.backend.service;

import com.example.backend.entity.DanhMuc;
import java.util.List;
import java.util.Optional;

public interface DanhMucService {
    List<DanhMuc> getAll();
    Optional<DanhMuc> getById(Integer id);
    DanhMuc save(DanhMuc danhMuc);
    void delete(Integer id);
}

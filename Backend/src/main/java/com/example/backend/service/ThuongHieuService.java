package com.example.backend.service;

import com.example.backend.entity.ThuongHieu;
import java.util.List;
import java.util.Optional;

public interface ThuongHieuService {
    List<ThuongHieu> getAll();
    Optional<ThuongHieu> getById(Integer id);
    ThuongHieu save(ThuongHieu thuongHieu);
    void delete(Integer id);
}
